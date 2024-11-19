package com.projects.ecommerce.product.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projects.ecommerce.utilts.Base;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Table(name = "products")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public final class Product extends Base {
	@Column(name = "product_title")
	private String productTitle;

	@Column(name = "image_url")
	private String imageUrl;

	@Column(name = "all_quantity")
	private Integer allQuantity;

	@Column(name = "discounted_price")
	private Double discountedPrice;

	@Column(name = "discount_percent")
	private Double discountPercent;

	@Column(columnDefinition = "decimal")
	private Double price;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sub_category_id")
	private SubCategory subCategory;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<ProductVariation> variations;

}










