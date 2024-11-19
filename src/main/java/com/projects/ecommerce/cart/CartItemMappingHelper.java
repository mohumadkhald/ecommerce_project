package com.projects.ecommerce.cart;


import com.projects.ecommerce.product.domain.Color;
import com.projects.ecommerce.product.domain.ProductVariation;
import com.projects.ecommerce.product.domain.Size;




public interface CartItemMappingHelper {
	public static CartItemDto map(final CartItem cartItem) {
		return CartItemDto.builder()
				.itemID(cartItem.getId())
				.totalPrice(cartItem.getPrice())
				.totalPriceDiscounted(cartItem.getQuantity()*cartItem.getProductVariation().getProduct().getDiscountedPrice())
				.ProductId(cartItem.getProductVariation().getProduct().getId())
				.imageUrl(cartItem.getProductVariation().getProduct().getImageUrl())
				.productTitle(cartItem.getProductVariation().getProduct().getProductTitle())
				.price(cartItem.getProductVariation().getProduct().getPrice())
				.discountedPrice(cartItem.getProductVariation().getProduct().getDiscountedPrice())
				.quantity(cartItem.getQuantity())
				.size(String.valueOf(cartItem.getProductVariation().getSize()))
				.color(String.valueOf(cartItem.getProductVariation().getColor()))
				.build();
	}
	public static CartItem map(final CartItemDto cartItemDto) {
		return CartItem.builder()
				.price(cartItemDto.getDiscountedPrice())
				.quantity(cartItemDto.getQuantity())
				.productVariation(ProductVariation.builder()
						.color(Color.valueOf(cartItemDto.getColor()))
						.size(Size.valueOf(cartItemDto.getColor()))
						.build())
				.build();
	}


}









