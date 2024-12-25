package com.projects.ecommerce.Auth.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequestDto {
        @NotBlank(message = "Last name cannot be empty or start space")
        @Pattern(regexp = "^[^0-9]{3,}$", message = "Last Name must be at least 3 characters long and cannot contain numbers")
        private String firstName;


        @NotBlank(message = "Last name cannot be empty or start space")
        @Pattern(regexp = "^[^0-9]{3,}$", message = "Last Name must be at least 3 characters long and cannot contain numbers")
        private String lastName;

//        @NotBlank(message = "Phone cannot be empty or start space")
//        @Pattern(regexp = "^01[0-2,5,9]{1}[0-9]{8}$", message = "Phone number must be 11 digits and start with 01 followed by 0, 1, 2, 5, or 9")
//        private String phone;

        @Column(unique = true)
        @NotBlank(message = "Email cannot be empty or start space")
        @Pattern(regexp = "^(.+)@(.+)$", message = "Email should be valid")
        private String email;

        @NotBlank(message = "Gender cannot be empty or start space")
        private String gender;

//        @NotBlank(message = "oldPassword cannot be empty or start space")
//        private String oldPassword;
//
//        @NotBlank(message = "Password cannot be empty or start space")
//        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$", message = "Password must contain at least one digit, one lowercase and one uppercase letter, and be at least 8 characters")
//        @NotNull
//        private String password;

}
