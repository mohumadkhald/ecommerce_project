package com.projects.ecommerce.user.repository;

import com.projects.ecommerce.user.model.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationRepo  extends JpaRepository<EmailVerification, Integer> {
    EmailVerification findByVerificationToken(String token);
}
