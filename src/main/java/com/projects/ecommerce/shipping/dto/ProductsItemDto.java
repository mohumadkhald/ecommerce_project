package com.projects.ecommerce.shipping.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.projects.ecommerce.product.dto.SubCategoryDto;
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
public class ProductsItemDto implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;
	private Integer productId;
	private String productTitle;
	private String imageUrl;
	private String sku;
	private Double price;
	private int discountPercent;
	
}










