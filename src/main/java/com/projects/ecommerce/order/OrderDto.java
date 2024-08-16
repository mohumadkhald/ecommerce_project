package com.projects.ecommerce.order;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class OrderDto {

    private Integer id;
    private Integer userId;
    private List<OrderItemDto> orderItems;
    private Double totalPrice;
    private String status;
    private PaymentInfoDto paymentInfo;
    private AddressDto shippingAddress;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
}
