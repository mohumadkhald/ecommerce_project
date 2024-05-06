package com.projects.ecommerce.shipping.domain;


import com.projects.ecommerce.order.domain.Cart;
import com.projects.ecommerce.shipping.domain.id.OrderItemId;
import com.projects.ecommerce.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "order_items")
@IdClass(OrderItemId.class)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public final class OrderItem extends AbstractMappedEntity implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "product_id", nullable = false, updatable = false)
	private Integer productId;

	@Column(name = "order_id")
	private Integer orderId;

	private boolean orderNow;

	private Double totalPrice;

	@Column(name = "ordered_quantity")
	private Integer orderedQuantity;

	@Id
	@Column(name = "cart_id", nullable = false, updatable = false)
	private Integer cartId;

//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "cart_id")
//	private Cart cart;
	
}










