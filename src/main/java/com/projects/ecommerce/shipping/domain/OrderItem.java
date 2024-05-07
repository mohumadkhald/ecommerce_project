package com.projects.ecommerce.shipping.domain;

import com.projects.ecommerce.order.domain.Cart;
import com.projects.ecommerce.product.domain.Product;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "order_items")
//@IdClass(OrderItemId.class)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public final class OrderItem extends AbstractMappedEntity implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;

//	@Column(name = "product_id", nullable = false, updatable = false)
//	private Integer productId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "product_id")
	private Product product;


	@Column(name = "order_id")
	private Integer orderId;

	private boolean orderNow;

	private Double totalPrice;

	@Column(name = "ordered_quantity")
	private Integer orderedQuantity;

	@Column(name = "cart_id", nullable = false, updatable = false)
	private Integer cartId;

	@OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ItemVariation> variations;

	@Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer item_id;


	//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "cart_id")
//	private Cart cart;
	
}










