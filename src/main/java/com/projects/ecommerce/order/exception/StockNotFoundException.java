package com.projects.ecommerce.order.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class StockNotFoundException extends RuntimeException {
    private final Map<String, Map<String, Object>> errors;

    public StockNotFoundException(Map<String, Map<String, Object>> errors, String message) {
        super(message);
        this.errors = errors;
    }
}