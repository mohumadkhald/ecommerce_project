package com.projects.ecommerce.Auth.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendResetPasswordEmail(String to, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Myapp" + " <" + "manager@ex.com" + ">");
        message.setTo(to);
        message.setSubject("Reset Password");
        message.setText("Your code is: " + resetToken);
        emailSender.send(message);
        System.out.println("Email sent successfully!");
    }

    public void sendVerificationEmail(String to, String verificationToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Verify Your Email");
        message.setText("Please click the following link to verify your email: http://localhost:8080/api/auth/verify-email?token=" + verificationToken);
        emailSender.send(message);
    }

}