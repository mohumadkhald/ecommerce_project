package com.projects.ecommerce.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Spec{
    @NotBlank(message = "Color cannot be null or empty")
    private String color;
    @NotBlank(message = "Size cannot be null or empty")
    private String size;
    private String img;
    @NotNull(message = "Quantity cannot be null") @Positive(message = "Quantity must be a positive integer")
    private Integer quantity;

    }

