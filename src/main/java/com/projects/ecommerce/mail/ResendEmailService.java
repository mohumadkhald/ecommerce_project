package com.projects.ecommerce.mail;

import com.resend.Resend;
import com.resend.services.emails.model.SendEmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("docker")
public class ResendEmailService implements EmailService {

    private final Resend resend;

    public ResendEmailService(@Value("${resend.api-key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    @Override
    public void sendResetPasswordEmail(String to, String token) {
        SendEmailRequest request = SendEmailRequest.builder()
                .from("MyApp <onboarding@resend.dev>")
                .to(to)
                .subject("Reset Password")
                .html("""
                            <html>
                              <body>
                                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;
                                            padding: 20px; border: 1px solid #ccc; border-radius: 10px;">
                                  <h2 style="color: #333; text-align: center;">Reset Password</h2>
                                  <p>Dear user,</p>
                                  <p>We received a request to reset your password. Use the code below to reset it.</p>
                        
                                  <div style="text-align: center; margin: 20px 0;">
                                    <p style="font-size: 24px; font-weight: bold;">
                                      %s
                                    </p>
                                  </div>
                        
                                  <p>If you didn't request a password reset, please ignore this email.</p>
                                  <p>Thank you,</p>
                                  <p>The Myapp Team</p>
                                </div>
                              </body>
                            </html>
                        """.formatted(token))
                .build();


        resend.emails().send(request);
    }

    @Override
    public void sendVerificationEmail(String to, String token) {
        String link = "https://myapp.com/api/auth/verify-email?token=" + token;

        SendEmailRequest request = SendEmailRequest.builder()
                .from("MyApp <onboarding@resend.dev>")
                .to(to)
                .subject("Verify Email")
                .html("<a href=\"" + link + "\">Verify Email</a>")
                .build();

        resend.emails().send(request);
    }
}