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
    public ResponseEntity<Map<String, String>> requestResetPassword(
            @RequestParam("email") @NotNull @Email String email) {

        // Find the user by email
        User user = userRepo.findByEmail(email);

        if (user != null) {
            // Check if the user already has a password reset entry
            PasswordReset passwordReset = user.getPasswordReset();

            if (passwordReset == null) {
                // If no password reset entry exists, create a new one
                passwordReset = new PasswordReset();
                passwordReset.setUser(user);
            }

            // Generate and set the reset code
            String resetCode = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
            passwordReset.setResetCode(resetCode);
            passwordReset.setTimeTry(0);

            // Set the reset code expiry time
            passwordReset.setResetCodeExpiry(LocalDateTime.now().plusHours(24)); // Expiry time 1 day

            // Send reset password email with the token
            emailService.sendResetPasswordEmail(email, resetCode);

            // Set How Many Times Send code
            passwordReset.setTimesSend(1);

            // Save or update the password reset entry
            user.setPasswordReset(passwordReset);
            user.setNeedsToSetPassword(false);
            userRepo.save(user);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Code created successfully");
            return ResponseEntity.ok(response);

        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Email " + email + " does not exist");
            return ResponseEntity.badRequest().body(response);
        }
    }


    @PostMapping("/reset")
    @Transactional
    public ResponseEntity<Map<String, String>> resetPassword(@RequestParam("email") String email, @Valid @RequestBody PasswordDto passwordDto) {
        User user = userRepo.findByEmail(email);

        if (user == null) {
            // Handle case when user is not found
            Map<String, String> response = new HashMap<>();
            response.put("message", "Email " + email + "Not Exist");
            return ResponseEntity.badRequest().body(response);
        }

        PasswordReset passwordReset = resetRepo.findByUser(user);
        if (passwordReset == null || !passwordReset.getResetCode().equals(passwordDto.getCode())) {
            // If no PasswordReset exists for the user or code is incorrect, handle it
            if (passwordReset == null) {
                // Create a new PasswordReset entity for the user
                passwordReset = PasswordReset.builder()
                        .user(user)
                        .resetCode(passwordDto.getCode())
                        .timeTry(1) // Initialize try_time to 1
                        .timesSend(1) // Initialize try_time-send to 1
                        .build();
            } else {
                // Increment try_time and check if it's 5
                int tries = passwordReset.getTimeTry() + 1;
                if (tries >= 3) {
                    String resetCode = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
                    // Generate a new reset code and reset try_time
                    passwordReset.setResetCode(resetCode);
                    passwordReset.setTimeTry(0);

                    // Send reset password email with the token
                    emailService.sendResetPasswordEmail(email, resetCode);
                    // Set How Many Times Send code
                    passwordReset.setTimesSend(passwordReset.getTimesSend()+1);
                    // Return appropriate response
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Incorrect code. Another Code Sent Check Email Address.");
                    return ResponseEntity.badRequest().body(response);
                } else {
                    passwordReset.setTimeTry(tries);
                }
            }

            // Save the PasswordReset entity
            resetRepo.save(passwordReset);

            // Return appropriate response
            Map<String, String> response = new HashMap<>();
            response.put("message", "Incorrect code. Please try again.");
            return ResponseEntity.badRequest().body(response);
        }

        // If code is correct, reset try_time and proceed with password reset
        passwordReset.setTimeTry(0);
        // Proceed with password reset logic

        user.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
        userRepo.save(user);
        resetRepo.deleteByUserId(user.getId());
        // Return success response
        Map<String, String> response = new HashMap<>();
        response.put("message", "The password has been reset");
        return ResponseEntity.ok(response);
    }

}
