package com.projects.ecommerce.controller;

import com.projects.ecommerce.config.JwtProvider;
import com.projects.ecommerce.exception.UserException;
import com.projects.ecommerce.model.User;
import com.projects.ecommerce.repo.UserRepo;
import com.projects.ecommerce.requests.LoginRequest;
import com.projects.ecommerce.response.AuthResponse;
import com.projects.ecommerce.service.CustomerServiceImplement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class UserController {
    private final Logger logger;

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final CustomerServiceImplement customerServiceImplement;
    @PostMapping("/register")
    public ResponseEntity<?> createUserHandler(@RequestBody User user) {
        try {
            logger.info("Registration request received for user: {}", user.getEmail());

            validateUserInput(user);

            if (userRepo.existsByEmail(user.getEmail())) {
                logger.warn("Email already used with another account: {}", user.getEmail());
                throw new UserException("Email already used with another account");
            }

            User createdUser = createUserFromRequest(user);
            User savedUser = userRepo.save(createdUser);

            Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtProvider.generateToken(authentication);
            AuthResponse authResponse = new AuthResponse(token, "Signup Success");

            logger.info("User registered successfully: {}", user.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
        } catch (UserException e) {
            logger.error("User registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred during user registration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }


    private void validateUserInput(User user) {
        // Implement validation logic for user input fields (e.g., email format, password strength)
        // Throw UserException if validation fails
    }

    private User createUserFromRequest(User user) {
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setFirstname(user.getFirstname());
        newUser.setLastname(user.getLastname());
        return newUser;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // Retrieve user details from the database based on the provided email
        UserDetails userDetails = customerServiceImplement.loadUserByUsername(loginRequest.getEmail());

        // Validate the provided password against the user's password
        if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email/password ");
        }

        // Authenticate the user
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String token = jwtProvider.generateToken(authentication);

        // Return the JWT token along with a success message
        return ResponseEntity.ok(new AuthResponse(token, "Login Success"));
    }



}
