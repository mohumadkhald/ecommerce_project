package com.projects.ecommerce.product.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projects.ecommerce.shipping.domain.ItemVariation;
import com.projects.ecommerce.shipping.domain.OrderItem;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
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

	@Column(name = "all_quantity")
	private Integer allQuantity;

	@Column(name = "discounted_price")
	private int discountedPrice;

	@Column(name = "discount_percent")
	private int discountPercent;

	@Column(columnDefinition = "decimal")
	private Double price;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sub_category_id")
	private SubCategory subCategory;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductVariation> variations;

	@JsonIgnore
	@OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<ItemVariation> itemVariations;
}










