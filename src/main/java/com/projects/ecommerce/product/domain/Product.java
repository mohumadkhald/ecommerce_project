package com.projects.ecommerce.product.domain;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

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
	
//	@Column(unique = true)
//	private String sku;

	@Column(name = "all-quantity")
	private Integer allQuantity;

	private int discountedPrice;

	private int discountPercent;

	@Column(columnDefinition = "decimal")
	private Double price;


	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id")
	private Category category;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductVariation> variations;
}










