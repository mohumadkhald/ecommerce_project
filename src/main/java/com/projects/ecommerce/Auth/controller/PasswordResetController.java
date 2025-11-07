package com.projects.ecommerce.Auth.controller;


import com.projects.ecommerce.Auth.dto.PasswordDto;
import com.projects.ecommerce.Auth.service.EmailService;
import com.projects.ecommerce.user.model.PasswordReset;
import com.projects.ecommerce.user.repository.ResetRepo;
import com.projects.ecommerce.user.model.User;
import com.projects.ecommerce.user.repository.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class PasswordResetController {
    private UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ResetRepo resetRepo;

    @PersistenceContext
    private EntityManager entityManager;

    @PostMapping("/send-reset")
    @Transactional
    public ResponseEntity<Map<String, String>> requestResetPassword(
            @RequestParam("email") @NotNull @Email String email) {

        Map<String, String> response = new HashMap<>();
        User user = userRepo.findByEmail(email);

        if (user == null) {
            response.put("message", "Email " + email + " does not exist");
            return ResponseEntity.badRequest().body(response);
        }

        PasswordReset passwordReset = resetRepo.findByUser(user);
        LocalDateTime now = LocalDateTime.now();

        // If user already has a password reset entry
        if (passwordReset != null) {
            // Check if reached max send limit (3 times)
            if (passwordReset.getTimesSend() >= 3) {
                LocalDateTime lastSent = passwordReset.getLastSentAt();
                if (lastSent != null && lastSent.plusHours(1).isAfter(now)) {
                    long minutesLeft = Duration.between(now, lastSent.plusHours(1)).toMinutes();
                    response.put("message", "You have reached the maximum reset code requests. Try again after " + minutesLeft + " minutes.");
                    return ResponseEntity.badRequest().body(response);
                } else {
                    // Cooldown passed → reset counters
                    passwordReset.setTimesSend(0);
                    passwordReset.setTimeTry(0);
                }
            }
        } else {
            // No existing reset record
            passwordReset = new PasswordReset();
            passwordReset.setUser(user);
        }

        // Generate new reset code
        String resetCode = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        passwordReset.setResetCode(resetCode);
        passwordReset.setTimeTry(0);
        passwordReset.setTimesSend(0);
        passwordReset.setResetCodeExpiry(LocalDateTime.now().plusHours(24)); // 1-day expiry

        // Increment send counter
        passwordReset.setTimesSend(passwordReset.getTimesSend() + 1);
        passwordReset.setLastSentAt(now);

        // Send email
        emailService.sendResetPasswordEmail(email, resetCode);

        // Save to DB
        resetRepo.save(passwordReset);

        response.put("message", "Reset code sent successfully. Check your email.");
        return ResponseEntity.ok(response);
    }



    @PostMapping("/reset")
    @Transactional
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestParam("email") String email,
            @Valid @RequestBody PasswordDto passwordDto) {

        Map<String, String> response = new HashMap<>();
        User user = userRepo.findByEmail(email);

        if (user == null) {
            response.put("message", "Email " + email + " not exist");
            return ResponseEntity.badRequest().body(response);
        }

        PasswordReset passwordReset = resetRepo.findByUser(user);
        LocalDateTime now = LocalDateTime.now();

        // If user has a reset record
        if (passwordReset != null) {
            // Check if user reached max code sends (3 times)
            if (passwordReset.getTimesSend() >= 3) {
                LocalDateTime lastSent = passwordReset.getLastSentAt();
                if (lastSent != null && lastSent.plusHours(1).isAfter(now)) {
                    long minutesLeft = Duration.between(now, lastSent.plusHours(1)).toMinutes();
                    response.put("message", "You have reached the maximum reset attempts. Try again after " + minutesLeft + " minutes.");
                    return ResponseEntity.badRequest().body(response);
                } else {
                    // If cooldown passed, reset counters
                    passwordReset.setTimesSend(0);
                    passwordReset.setTimeTry(0);
                    resetRepo.save(passwordReset);
                }
            }
        }

        // If no existing record, create one
        if (passwordReset == null) {
            passwordReset = PasswordReset.builder()
                    .user(user)
                    .resetCode(passwordDto.getCode())
                    .timeTry(1)
                    .timesSend(1)
                    .lastSentAt(now)
                    .build();
            resetRepo.save(passwordReset);
            response.put("message", "Incorrect code. Please try again.");
            return ResponseEntity.badRequest().body(response);
        }

        // Handle incorrect code
        if (!passwordReset.getResetCode().equals(passwordDto.getCode())) {
            int tries = passwordReset.getTimeTry() + 1;
            passwordReset.setTimeTry(tries);

            if (tries >= 3) {
                if (passwordReset.getTimesSend() >= 3) {
                    response.put("message", "You have reached the maximum reset attempts. Try again after 1 hour.");
                    return ResponseEntity.badRequest().body(response);
                }

                // Generate a new code
                String resetCode = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
                passwordReset.setResetCode(resetCode);
                passwordReset.setTimeTry(0);
                passwordReset.setTimesSend(passwordReset.getTimesSend() + 1);
                passwordReset.setLastSentAt(now);

                // Send the email
                emailService.sendResetPasswordEmail(email, resetCode);

                resetRepo.save(passwordReset);
                response.put("message", "Incorrect code. Another code has been sent. Check your email.");
                return ResponseEntity.badRequest().body(response);
            }

            resetRepo.save(passwordReset);
            response.put("message", "Incorrect code. Please try again.");
            return ResponseEntity.badRequest().body(response);
        }

        // If code correct, reset password
        passwordReset.setTimeTry(0);
        resetRepo.save(passwordReset);

        user.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
        userRepo.save(user);
        resetRepo.deleteByUserId(user.getId());

        response.put("message", "Password has been reset successfully.");
        return ResponseEntity.ok(response);
    }


}
