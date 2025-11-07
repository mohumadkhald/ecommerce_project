package com.projects.ecommerce.order.model;

import lombok.Data;

@Data
public class OrderStatusUpdateRequest {
    private OrderStatus status;
}
