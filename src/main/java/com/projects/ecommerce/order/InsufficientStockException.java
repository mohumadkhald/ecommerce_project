package com.projects.ecommerce.order;

import lombok.Getter;

public class InsufficientStockException extends RuntimeException {
    @Getter
    private final String productVariation;
    @Getter
    private final Integer productId;
    private final String message;

    public InsufficientStockException(String productVariation, Integer productId, String message) {
        super(message);
        this.productVariation = productVariation;
        this.productId = productId;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

