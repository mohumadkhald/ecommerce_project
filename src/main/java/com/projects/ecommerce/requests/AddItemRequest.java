package com.projects.ecommerce.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddItemRequest {
    private Long productId;

    private String size;

    private int quantity;

    private Integer price;
    private Long userId;
}
