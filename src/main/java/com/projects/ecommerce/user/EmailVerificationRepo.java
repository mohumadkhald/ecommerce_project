package com.projects.ecommerce.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationRepo  extends JpaRepository<EmailVerification, Integer> {
    EmailVerification findByVerificationToken(String token);
}
