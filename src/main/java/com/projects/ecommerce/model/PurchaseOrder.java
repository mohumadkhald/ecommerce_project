package com.projects.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "purchase_order") // Specify the table name here
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private List<OrderItem> orderItemList = new ArrayList<>();

    private LocalDateTime orderDate;

    private LocalDateTime deliveryDate;

    @OneToOne
    private Address shippingAddress;

    @Embedded
    private PaymentInfo payment = new PaymentInfo();

    private double totalPrice;

    private Integer totalDiscountedPrice;

    private Integer discount;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private int totalItem;

    private LocalDateTime createdAt;
}

