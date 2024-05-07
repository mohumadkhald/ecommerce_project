package com.projects.ecommerce.shipping.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.projects.ecommerce.product.dto.SubCategoryDto;
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
public class OrderItemsDto implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;
	
//	private Integer productId;
@JsonProperty("Product")
@JsonInclude(JsonInclude.Include.NON_NULL)
private ProductDto product;

	private Integer orderId;
	private Integer orderedQuantity;

//	private Map<String, List<String>> colorsAndSizes; // Map to store colors and associated sizes

	private Map<String, Map<String, String>> colorsAndSizesWithQuantity;

	private boolean orderNow;
	private Integer cartId;
	private Double totalPrice;

}










