package com.projects.ecommerce.product.domain;


import com.projects.ecommerce.utilts.Base;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

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

//	@Column(name = "image_url")
//	private String imageUrl;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductImage> images = new ArrayList<>();


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










