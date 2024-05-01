package com.projects.ecommerce.Auth.controller;


import com.projects.ecommerce.Auth.dto.PasswordDto;
import com.projects.ecommerce.Auth.service.EmailService;
import com.projects.ecommerce.user.PasswordReset;
import com.projects.ecommerce.user.ResetRepo;
import com.projects.ecommerce.user.User;
import com.projects.ecommerce.user.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
    public ResponseEntity<String> resetPassword(@RequestParam("code") String code, @Valid @RequestBody PasswordDto passwordDto) {
        User user = userRepo.findByPasswordReset_ResetCode(code);

        if (user != null && user.getPasswordReset() != null && user.getPasswordReset().getResetCodeExpiry() != null && user.getPasswordReset().getResetCodeExpiry().isAfter(LocalDateTime.now())) {
            // Update user's password and clear reset token
            user.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
            user.getPasswordReset().setResetCode(null);
            user.getPasswordReset().setResetCodeExpiry(null);
            userRepo.save(user);

            // Manually flush changes to synchronize with the database
            entityManager.flush();

            // Remove the PasswordReset entry associated with the user
            resetRepo.deleteByUserId(user.getId());
            return ResponseEntity.ok("Password reset successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired code.");
        }
    }







}