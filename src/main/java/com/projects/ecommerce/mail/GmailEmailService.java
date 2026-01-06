package com.projects.ecommerce.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class GmailEmailService implements EmailService {

    private final JavaMailSender mailSender;

    public GmailEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendResetPasswordEmail(String to, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("MyApp <yourgmail@gmail.com>");
            helper.setTo(to);
            helper.setSubject("Reset Password");
            helper.setText(buildResetEmail(token), true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendVerificationEmail(String to, String token) {
        String link = "http://localhost:8080/api/auth/verify-email?token=" + token;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Verify Email");
            helper.setText(buildVerifyEmail(link), true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildResetEmail(String token) {
        return "<h2>Reset Password</h2><p>Code: <b>" + token + "</b></p>";
    }

    private String buildVerifyEmail(String link) {
        return "<a href=\"" + link + "\">Verify Email</a>";
    }
}