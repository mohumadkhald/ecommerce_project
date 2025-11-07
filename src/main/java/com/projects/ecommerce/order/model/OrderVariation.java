package com.projects.ecommerce.order.model;

import com.projects.ecommerce.cart.CartItem;
import com.projects.ecommerce.product.domain.AbstractMappedEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "order_variations")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public final class OrderVariation extends AbstractMappedEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variation_id", unique = true, nullable = false, updatable = false)
    private Integer Id;

    @Column(name = "product_title")
    private String productTitle;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "discounted_price")
    private Double discountedPrice;

    @Column(name = "discount_percent")
    private Double discountPercent;

    @Column(columnDefinition = "decimal")
    private Double price;

    @Column(name = "color")
    private String color;

    @Column(name = "size")
    private String size;

    @Column(name = "quantity")
    private int quantity;

    private String img;

    @OneToMany(mappedBy = "orderVariation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> cartItems;

}
