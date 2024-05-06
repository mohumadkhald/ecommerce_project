package com.projects.ecommerce.utilts.traits;

import com.projects.ecommerce.user.dto.UserResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class ApiTrait {

    public static ResponseEntity<?> successMessage(String message, HttpStatus code) {
        return ResponseEntity.status(code)
                .body(new ApiResponse(message, new HashMap<>(), new HashMap<>()));
    }

    public static ResponseEntity<?> errorMessage(HashMap<String, String> errors, String message, HttpStatus code) {
        return ResponseEntity.status(code)
                .body(new ApiResponse(message, errors, new HashMap<>()));
    }

    public static ResponseEntity<?> data(List<UserResponseDto> data, String message, HttpStatus code) {
        return ResponseEntity.status(code)
                .body(new ApiResponse(message, new HashMap<>(), data));
    }

    public static ResponseEntity<?> handleUserList(List<UserResponseDto> users, String messageNotFound) {
        if (users.isEmpty()) {
            return data(users, "No Following", HttpStatus.OK);
        } else {
            return data(users, "The Data Retrieved Success", HttpStatus.OK);
        }
    }

    // Inner class representing the API response structure
    static class ApiResponse {
        private final String message;
        private final Object errors;
        private final Object data;

        public ApiResponse(String message, Object errors, Object data) {
            this.message = message;
            this.errors = errors;
            this.data = data;
        }

        // Getters for message, errors, and data
        // You can generate these using your IDE or write manually
        public String getMessage() {
            return message;
        }

        public Object getErrors() {
            return errors;
        }

        public Object getData() {
            return data;
        }
    }
}
