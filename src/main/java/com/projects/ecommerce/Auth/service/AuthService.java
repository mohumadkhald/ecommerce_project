package com.projects.ecommerce.Auth.service;


import com.projects.ecommerce.Auth.dto.AuthResponse;
import com.projects.ecommerce.Auth.dto.LoginRequestDto;
import com.projects.ecommerce.Auth.dto.RegisterRequestDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    // Method Register User to Website
    AuthResponse register(RegisterRequestDto request);

    ResponseEntity<?> resendVerificationEmail(String email) throws Exception;

    //Method Log in For Website
    AuthResponse login(LoginRequestDto request) throws Exception;

}