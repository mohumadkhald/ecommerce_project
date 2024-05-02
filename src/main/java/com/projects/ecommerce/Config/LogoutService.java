package com.projects.ecommerce.Config;


import com.projects.ecommerce.Auth.token.Token;
import com.projects.ecommerce.Auth.token.TokenRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogoutService implements LogoutHandler {

    private final TokenRepo tokenRepo;

    public LogoutService(TokenRepo tokenRepo) {
        this.tokenRepo = tokenRepo;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String jwt = authorization.substring(7);
            Token storedToken = tokenRepo.findByToken(jwt).orElse(null);
            if (storedToken != null) {
                storedToken.setExpired(true);
                storedToken.setRevoked(true);
                storedToken.setExpirationDate(LocalDateTime.now());
                // Save the changes back to the repository
                tokenRepo.save(storedToken);
            }
        }
    }

}