package com.projects.ecommerce.order;

import lombok.Data;

@Data
public class OrderStatusUpdateRequest {
    private OrderStatus status;
}
