package com.projects.ecommerce.mail;

public interface EmailService {
    void sendResetPasswordEmail(String to, String token);
    void sendVerificationEmail(String to, String token);
}
