package com.projects.ecommerce.shipping.helper;


import com.projects.ecommerce.shipping.domain.OrderItem;
import com.projects.ecommerce.shipping.dto.OrderItemDto;

public interface OrderItemMappingHelper {

	public static OrderItemDto map(final OrderItem orderItem) {

		return OrderItemDto.builder()
				.productId(orderItem.getProductId())
				.orderId(orderItem.getOrderId())
				.orderedQuantity(orderItem.getOrderedQuantity())
				.totalPrice(orderItem.getTotalPrice())
				.orderNow(orderItem.isOrderNow())
				.cartId(
						orderItem.getCartId()
				)
				.build();
	}


	
	public static OrderItem map(final OrderItemDto orderItemDto) {
		return OrderItem.builder()
				.productId(orderItemDto.getProductId())
				.orderId(orderItemDto.getOrderId())
				.orderedQuantity(orderItemDto.getOrderedQuantity())
				.totalPrice(orderItemDto.getTotalPrice())
				.cartId(orderItemDto.getCartId())
				.orderNow(orderItemDto.isOrderNow())
				.build();
	}

}










