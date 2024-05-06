package com.projects.ecommerce.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.projects.ecommerce.shipping.domain.OrderItem;
import com.projects.ecommerce.shipping.dto.OrderItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CartDto implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;
	
	private Integer cartId;
	private Integer userId;
	
	@JsonInclude(Include.NON_NULL)
	private Set<OrderItemDto> orderItemDtos;


	
}










