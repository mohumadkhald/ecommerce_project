package com.projects.ecommerce.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class OrderItemDto {
    private List<ProductVariationDto> productVariations; // Updated to hold a list of variations
    private String productName;
    private String img;
    private Double price;
}
