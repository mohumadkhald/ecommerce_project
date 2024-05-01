package com.projects.ecommerce.Auth.expetion;

public class NotAuthorizeException extends RuntimeException {
    public NotAuthorizeException(String message) {
        super(message);
    }
}
