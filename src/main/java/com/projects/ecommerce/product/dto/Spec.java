package com.projects.ecommerce.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record Spec(
        @NotBlank(message = "Color cannot be null or empty") String color,
        @NotBlank(message = "Size cannot be null or empty") String size,
        @NotNull(message = "Quantity cannot be null") @Positive(message = "Quantity must be a positive integer") Integer quantity) {
}
