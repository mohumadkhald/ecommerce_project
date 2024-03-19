package com.projects.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id") // Corrected the column name to match the database
    @JsonBackReference
    private Cart cart;

    @ManyToOne
    private Product product;

    private String size;
    private int quantity;
    private Integer price;
    private Integer discountedPrice;
    private Long userId;
}
