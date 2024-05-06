package com.projects.ecommerce.shipping.dto;

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
public class OrderItemDto implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;
	
	private Integer productId;
	private Integer orderId;
	private Integer orderedQuantity;

	private String color;
	private String size;

	private boolean orderNow;
	private Integer cartId;
	private Double totalPrice;
	
}










