package com.projects.ecommerce.product.domain;

import com.projects.ecommerce.cart.CartItem;
import com.projects.ecommerce.order.OrderItem;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "product_variations")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"product"})
@Data
@Builder
public final class ProductVariation extends AbstractMappedEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variation_id", unique = true, nullable = false, updatable = false)
    private Integer Id;

    @Column(name = "color")
    @Enumerated(EnumType.STRING)
    private Color color;

    @Column(name = "size")
    @Enumerated(EnumType.STRING)
    private Size size;

    @Column(name = "quantity")
    private int quantity;

    private String img;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    // Optional: Bidirectional relationship with OrderItem
    @OneToMany(mappedBy = "productVariation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "productVariation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> cartItems;
}
