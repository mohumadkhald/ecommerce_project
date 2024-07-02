package com.projects.ecommerce.cart;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartRequest {
    private Integer productId;
    @NotNull
    private int quantity;
    @NotNull
    private String size;
    @NotNull
    private String color;
    private Double price;
}
