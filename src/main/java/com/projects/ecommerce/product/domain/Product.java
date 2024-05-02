package com.projects.ecommerce.product.domain;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"category"})
@Data
@Builder
public final class Product extends AbstractMappedEntity implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id", unique = true, nullable = false, updatable = false)
	private Integer productId;
	
	@Column(name = "product_title")
	private String productTitle;
	
	@Column(name = "image_url")
	private String imageUrl;
	
	@Column(unique = true)
	private String sku;
	
	@Column(name = "price_unit", columnDefinition = "decimal")
	private Double priceUnit;
	
	@Column(name = "quantity")
	private Integer quantity;

	private int discountedPrice;

	private int discountPercent;

	private String color;

	private int price;

	@ManyToOne
	@JoinColumn(name = "size_id")
	private Size size;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id")
	private Category category;
	
}










