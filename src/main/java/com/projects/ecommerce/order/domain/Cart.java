package com.projects.ecommerce.order.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projects.ecommerce.shipping.domain.OrderItem;
import com.projects.ecommerce.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "carts")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"orderItems"})
@Data
@Builder
public final class Cart extends AbstractMappedEntity implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_id", unique = true, nullable = false, updatable = false)
	private Integer cartId;
	
	@Column(name = "user_id")
	private Integer userId;


//	@JsonIgnore
//	@OneToMany(mappedBy = "cart", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	private Set<OrderItem> orderItems;
	
}










