package com.projects.ecommerce.user;

import com.projects.ecommerce.Auth.expetion.AuthenticationnException;
import com.projects.ecommerce.traits.ApiTrait;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;

@ControllerAdvice
@RestController

public class UserException {

    public UserException(ApiTrait apiTrait) {
    }


    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<?> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex, WebRequest request) {
        HashMap<String, String> error = new HashMap<>();
        error.put("email", "Email Already Exists"); // Assuming "email" is the field causing the error
        return  ApiTrait.errorMessage(error, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        HashMap<String, String> error = new HashMap<>();
        error.put("user", "User Not Found"); // Assuming "user" is the field causing the error
        return  ApiTrait.errorMessage(error, ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        var errors = new HashMap<String, String>();
        exception.getBindingResult().getAllErrors().forEach(
                error -> {
                    var fieldName = ((FieldError) error).getField();
                    var errMsg = error.getDefaultMessage();
                    errors.put(fieldName, errMsg);
                }
        );
        return ApiTrait.errorMessage(errors, "Validation Failed", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationnException.class)
    public ResponseEntity<?> handleAuthException(AuthenticationnException ex, WebRequest request) {
        HashMap<String, String> error = new HashMap<>();
        error.put("Problem", ex.getMessage());
        return  ApiTrait.errorMessage(error, "Authentication Failed", HttpStatus.BAD_REQUEST);
    }

}