package com.projects.ecommerce.Auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendResetPasswordEmail(String to, String resetToken) {
        MimeMessage message = emailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("Myapp <manager@ex.com>");
            helper.setTo(to);
            helper.setSubject("Reset Password");
            helper.setText(buildEmailContent(resetToken), true); // true indicates HTML

            emailSender.send(message);
            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send email.");
        }
    }

    private String buildEmailContent(String resetToken) {
        return "<html>" +
            "<body>" +
            "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ccc; border-radius: 10px;\">" +
            "<h2 style=\"color: #333; text-align: center;\">Reset Password</h2>" +
            "<p>Dear user,</p>" +
            "<p>We received a request to reset your password. Use the code below to reset it.</p>" +
            "<div style=\"text-align: center; margin: 20px 0;\">" +
            "<p style=\"font-size: 24px; font-weight: bold;\">" + resetToken + "</p>" +
            "</div>" +
            "<p>If you didn't request a password reset, please ignore this email.</p>" +
            "<p>Thank you,</p>" +
            "<p>The Myapp Team</p>" +
            "</div>" +
            "</body>" +
            "</html>";
    }

    public void sendVerificationEmail(String to, String verificationToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Verify Your Email");
        message.setText("Please click the following link to verify your email: http://localhost:8080/api/auth/verify-email?token=" + verificationToken);
        emailSender.send(message);
    }
}



