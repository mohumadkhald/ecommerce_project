package com.projects.ecommerce.user.service;

import com.projects.ecommerce.Auth.dto.ChangePasswordDto;
import com.projects.ecommerce.Auth.dto.RegisterRequestDto;
import com.projects.ecommerce.Auth.dto.UpdateUserRequestDto;
import com.projects.ecommerce.user.dto.UserDto;
import com.projects.ecommerce.user.dto.UserResponseDto;
import com.projects.ecommerce.user.model.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

public interface UserService {

    Integer findUserIdByJwt(String jwt);


    UserDto findById(Integer id);

    // Method Create User
    UserResponseDto registerUser(RegisterRequestDto dto);

    // Method Get User By ID
    ResponseEntity<?> getUserByIdResponse(Integer id);

    // Method Get User By Email
    ResponseEntity<?> getUserByEmailResponse(String email);

    // Method search user
    ResponseEntity<?> searchUser(String query);

    ResponseEntity<Page<?>> getAllUsers(int page, int pageSize, Sort sort);

    ResponseEntity<?> updateUser(Integer id, UpdateUserRequestDto dto);


    User findByEmail(String email);

    void save(User newUser);

    User findByUserId(Integer userID);

    void updateUserPhoto(Integer userId, String imageUrl);

    ResponseEntity<?> changePassword(Integer id, @Valid ChangePasswordDto changePasswordDto);
}
