package com.projects.ecommerce.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private List<ProductVariationDto> productVariations; // Updated to hold a list of variations
    private String productName;
    private String img;
    private Double price;
    private Double discount;
    private Double totalPrice;
}
