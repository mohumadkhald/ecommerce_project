package com.projects.ecommerce.order.dto;

import com.projects.ecommerce.user.dto.UserDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class OrderDtoAdmin {

    private Integer id;
    private UserDto user;
    private List<OrderItemDto> orderItems;
    private Double totalPrice;
    private String status;
    private PaymentInfoDto paymentInfo;
    private AddressDto shippingAddress;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
}
