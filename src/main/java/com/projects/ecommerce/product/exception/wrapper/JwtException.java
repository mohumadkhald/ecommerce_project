package com.projects.ecommerce.product.exception.wrapper;

public class JwtException extends RuntimeException {
    public JwtException(String message) {
        super(message);
    }
}
