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
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public ResponseEntity<String> requestResetPassword(@RequestParam("email") String email) {

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

            // Save or update the password reset entry
            user.setPasswordReset(passwordReset);
            userRepo.save(user);

            // Send reset password email with the token
            emailService.sendResetPasswordEmail(email, resetCode);

            return ResponseEntity.ok("Password reset sent successfully.");
        } else {
            return ResponseEntity.badRequest().body("User not found.");
        }
    }



    @PostMapping("/reset")
    @Transactional
    public ResponseEntity<String> resetPassword(@RequestParam("email") String email, @Valid @RequestBody PasswordDto passwordDto) {
        User user = userRepo.findByEmail(email);

        if (user == null) {
            // Handle case when user is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
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
                        .build();
            } else {
                // Increment try_time and check if it's 5
                int tries = passwordReset.getTimeTry() + 1;
                if (tries >= 3) {
                    String resetCode = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
                    // Generate a new reset code and reset try_time
                    passwordReset.setResetCode(resetCode);
                    passwordReset.setTimeTry(0);
                } else {
                    passwordReset.setTimeTry(tries);
                }
            }

            // Save the PasswordReset entity
            resetRepo.save(passwordReset);

            // Return appropriate response
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect code. Please try again.");
        }

        // If code is correct, reset try_time and proceed with password reset
        passwordReset.setTimeTry(0);
        // Proceed with password reset logic

        user.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
        userRepo.save(user);
        resetRepo.deleteByUserId(user.getId());
        // Return success response
        return ResponseEntity.ok("Password reset successfully.");

    }

}