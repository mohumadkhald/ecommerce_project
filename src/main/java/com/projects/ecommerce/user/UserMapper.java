package com.projects.ecommerce.user;

import com.projects.ecommerce.Auth.dto.RegisterRequestDto;

import com.projects.ecommerce.user.dto.UserResponseDto;
import com.projects.ecommerce.user.model.AccountStatus;
import com.projects.ecommerce.user.model.EmailVerification;
import com.projects.ecommerce.user.model.Role;
import com.projects.ecommerce.user.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    // Method to map RegisterRequestDto to User entity
    public User toUser(RegisterRequestDto dto) {
        if (dto == null) {
            throw new NullPointerException("The RegisterRequestDto is Null");
        }
        var user = new User();
        user.setFirstname(dto.getFirstname());
        user.setLastname(dto.getLastname());
        user.setEmail(dto.getEmail());
        user.setGender(dto.getGender());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.USER);

        // Create and set account status
        AccountStatus accountStatus = new AccountStatus();
        accountStatus.setAccountNonExpired(true);
        accountStatus.setAccountNonLocked(true);
        accountStatus.setCredentialsNonExpired(true);
        user.setAccountStatus(accountStatus);

        user.setPhone(dto.getPhone());
        return user;
    }

    // Method to map User entity to UserResponseDto
    public UserResponseDto toUserResponseDto(User user) {
        if (user == null) {
            throw new NullPointerException("The User entity is Null");
        }
        EmailVerification emailVerification = user.getEmailVerification();
        return new UserResponseDto(user.getId(),
                user.getFirstname() + " " + user.getLastname(),
                user.getGender(),
                user.getEmail(),
                emailVerification != null && emailVerification.isEmailVerified()               // Map account status fields
        );
    }
}
