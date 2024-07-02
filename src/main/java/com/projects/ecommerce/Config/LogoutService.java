package com.projects.ecommerce.Config;


import com.projects.ecommerce.Auth.token.Token;
import com.projects.ecommerce.Auth.token.TokenRepo;
import com.projects.ecommerce.user.dto.UserDto;
import com.projects.ecommerce.user.model.User;
import com.projects.ecommerce.user.repository.UserRepo;
import com.projects.ecommerce.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LogoutService implements LogoutHandler {

    private final TokenRepo tokenRepo;
    private final UserService userService;

    public LogoutService(TokenRepo tokenRepo, UserService userService) {
        this.tokenRepo = tokenRepo;
        this.userService = userService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String jwt = authorization.substring(7);
            Integer userID = userService.findUserIdByJwt(authorization);
            User user = userService.findByUserId(userID);
            if (user.isO2Auth()){
                user.setO2Auth(false);
                userService.save(user);
            }
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