package com.projects.ecommerce.order.helper;


import com.projects.ecommerce.order.domain.Order;
import com.projects.ecommerce.order.dto.OrderDto;
import com.projects.ecommerce.user.dto.UserDto;
import com.projects.ecommerce.user.model.User;

public interface OrderMappingHelper {

	public static OrderDto map(final Order order) {
		if (order == null) {
			return null; // or throw an exception, depending on your requirements
		}

		UserDto userDto = null;
		if (order.getUser() != null) {
			userDto = UserDto.builder()
					.userId(order.getUser().getId())
					.build();
		}

		return OrderDto.builder()
				.orderId(order.getOrderId())
				.orderDate(order.getOrderDate())
				.orderDesc(order.getOrderDesc())
				.orderFee(order.getOrderFee())
				.userDto(userDto)
				.build();
	}

	public static Order map(final OrderDto orderDto) {
		if (orderDto == null) {
			return null; // or throw an exception, depending on your requirements
		}

		User user = null;
		if (orderDto.getUserDto() != null) {
			user = User.builder()
					.id(orderDto.getUserDto().getUserId())
					.build();
		}

		return Order.builder()
				.orderId(orderDto.getOrderId())
				.orderDate(orderDto.getOrderDate())
				.orderDesc(orderDto.getOrderDesc())
				.orderFee(orderDto.getOrderFee())
				.user(user)
				.build();
	}
	
	
	
}










