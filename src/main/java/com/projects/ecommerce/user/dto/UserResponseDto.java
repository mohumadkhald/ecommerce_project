package com.projects.ecommerce.user.dto;

public record UserResponseDto(Integer id, String username,  String gender, String email, boolean verify, String date, String role) {
}