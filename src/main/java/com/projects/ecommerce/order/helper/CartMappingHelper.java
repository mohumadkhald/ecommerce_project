package com.projects.ecommerce.order.helper;


import com.projects.ecommerce.order.domain.Cart;
import com.projects.ecommerce.order.dto.CartDto;
import com.projects.ecommerce.order.dto.CartRequestDto;

public interface CartMappingHelper {
	
	public static CartDto map(final Cart cart) {
		return CartDto.builder()
				.cartId(cart.getCartId())
				.userId(cart.getUserId())
				.build();
	}

	public static CartRequestDto map1(final Cart cart) {
		return CartRequestDto.builder()
				.cartId(cart.getCartId())
				.userId(cart.getUserId())
				.build();
	}

	public static Cart map(final CartDto cartDto) {
		return Cart.builder()
				.cartId(cartDto.getCartId())
				.userId(cartDto.getUserId())
				.build();
	}
	public static Cart map(final CartRequestDto cartRequestDto) {
		return Cart.builder()
				.cartId(cartRequestDto.getCartId())
				.userId(cartRequestDto.getUserId())
				.build();
	}
	
	
}










