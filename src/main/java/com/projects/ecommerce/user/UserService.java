package com.projects.ecommerce.user;

import com.projects.ecommerce.Auth.dto.RegisterRequestDto;
import com.projects.ecommerce.user.dto.UserResponseDto;
import org.springframework.http.ResponseEntity;

public interface UserService {

    Integer findUserIdByJwt(String jwt);

    User findById(Integer id);

    // Method Create User
    UserResponseDto registerUser(RegisterRequestDto dto);

    // Method Get User By ID
    ResponseEntity<?> getUserByIdResponse(Integer id);

    // Method Get User By Email
    ResponseEntity<?> getUserByEmailResponse(String email);

    // Method search user
    ResponseEntity<?> searchUser(String query);

    ResponseEntity<?> getAllUsers();

    ResponseEntity<?> updateUser(Integer id, RegisterRequestDto dto);




}
