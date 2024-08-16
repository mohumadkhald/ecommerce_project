package com.projects.ecommerce.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class OrderItemDto {
    private ProductVariationDto productVariation;
    private String productName;
    private String img;
    private Double price;
}
