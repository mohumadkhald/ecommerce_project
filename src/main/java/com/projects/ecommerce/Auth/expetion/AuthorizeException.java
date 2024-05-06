package com.projects.ecommerce.Auth.expetion;


import com.projects.ecommerce.utilts.traits.ApiTrait;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;

@ControllerAdvice
@RestController

public class AuthorizeException {

    public AuthorizeException(ApiTrait apiTrait) {
    }



    @ExceptionHandler(NotAuthorizeException.class)
    public ResponseEntity<?> handleUserNotAuthException(NotAuthorizeException ex, WebRequest request) {
        HashMap<String, String> error = new HashMap<>();
        return  ApiTrait.errorMessage(error, ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }



}