package com.projects.ecommerce.Config;


import com.projects.ecommerce.Auth.token.TokenRepo;
import com.projects.ecommerce.user.model.User;
import com.projects.ecommerce.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class LogoutService implements LogoutHandler {

    private final TokenRepo tokenRepo;
    private final UserService userService;
    private final JwtService jwtService;

    public LogoutService(TokenRepo tokenRepo, UserService userService, JwtService jwtService) {
        this.tokenRepo = tokenRepo;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String jwt = authorization.substring(7);
            Integer userID = userService.findUserIdByJwt(authorization);
            User user = userService.findByUserId(userID);
            if (user.isO2Auth()) {
                user.setO2Auth(false);
                userService.save(user);
            }

            if (jwtService.isTokenRevoked(jwt)) {
                writeJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Token already revoked");
                return;
            }

            jwtService.invalidateToken(jwt);
            writeJsonResponse(response, HttpServletResponse.SC_OK, "Token revoked successfully");
        } else {
            writeJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid authorization header");
        }
    }

    private void writeJsonResponse(HttpServletResponse response, int status, String message) {
        response.setStatus(status);
        response.setContentType("application/json");
        try {
            response.getWriter().write("{\"message\": \"" + message + "\"}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}