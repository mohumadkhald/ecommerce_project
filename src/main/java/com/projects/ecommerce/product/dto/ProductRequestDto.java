package com.projects.ecommerce.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductRequestDto implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;
	private Integer productId;
	private String productTitle;
	private String imageUrl;
	private String sku;
	private Double priceUnit;
	private Integer quantity;
	private Integer categoryId;
	
}










