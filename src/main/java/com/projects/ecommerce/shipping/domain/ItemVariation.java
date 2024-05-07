package com.projects.ecommerce.shipping.domain;

import com.projects.ecommerce.product.domain.AbstractMappedEntity;
import com.projects.ecommerce.product.domain.Color;
import com.projects.ecommerce.product.domain.Product;
import com.projects.ecommerce.product.domain.Size;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "cart_variations")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"product"})
@Data
@Builder
public final class ItemVariation extends AbstractMappedEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variation_id", unique = true, nullable = false, updatable = false)
    private Integer variationId;

    @Column(name = "color")
    @Enumerated(EnumType.STRING)
    private Color color;

    @Column(name = "size")
    @Enumerated(EnumType.STRING)
    private Size size;

    @Column(name = "quantity")
    private int quantity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    private OrderItem orderItem;
}
