package com.projects.ecommerce.cart;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.projects.ecommerce.product.domain.Product;
import com.projects.ecommerce.product.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CartItemDto implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;

	private Integer itemID;
	private Integer ProductId;
	private String productTitle;
	private Double price;
	private Double discountedPrice;
	private String imageUrl;
	private Integer quantity;
	private String size;
	private String color;
	private Double totalPrice;
	private Double totalPriceDiscounted;

}