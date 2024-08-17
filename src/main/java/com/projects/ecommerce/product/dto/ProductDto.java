package com.projects.ecommerce.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.projects.ecommerce.product.domain.Color;
import com.projects.ecommerce.product.domain.ProductVariation;
import com.projects.ecommerce.product.domain.Size;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductDto implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;
	private Integer productId;
	private String productTitle;
	private String imageUrl;
	private String sku;
	private List<ProductVariationDto> productVariations;
	private Map<String, List<String>> colorsAndSizes; // Map to store colors and associated sizes
	private Double price;
	private Double discountPercent;
	private Double discountPrice;
	private String email;
	private boolean inStock;


	@JsonProperty("subCategory")
	@JsonInclude(Include.NON_NULL)
	private SubCategoryDto subCategoryDto;
	
}










