package com.projects.ecommerce.shipping.helper;


import com.projects.ecommerce.product.domain.Color;
import com.projects.ecommerce.product.domain.Product;
import com.projects.ecommerce.product.domain.Size;
import com.projects.ecommerce.product.dto.ProductDto;
import com.projects.ecommerce.product.dto.ProductRequestDto;
import com.projects.ecommerce.shipping.domain.ItemVariation;
import com.projects.ecommerce.shipping.domain.OrderItem;
import com.projects.ecommerce.shipping.dto.OrderItemDto;
import com.projects.ecommerce.shipping.dto.OrderItemsDto;
import com.projects.ecommerce.shipping.dto.ProductsItemDto;
import com.projects.ecommerce.shipping.service.impl.OrderItemServiceImpl;

import java.util.*;


public interface OrderItemMappingHelper {

	static OrderItemsDto map(final OrderItem orderItem) {
		OrderItemsDto.OrderItemsDtoBuilder orderItemDtoBuilder = OrderItemsDto.builder()
//				.productId(orderItem.getProductId())
				.product(
						ProductsItemDto.builder()
								.productTitle(orderItem.getProduct().getProductTitle())
								.build()
				)
				.orderId(orderItem.getOrderId())
				.orderedQuantity(orderItem.getOrderedQuantity())
				.totalPrice(orderItem.getTotalPrice())
				.orderNow(orderItem.isOrderNow())
				.cartId(orderItem.getCartId());

// Extract color, size, and quantity from all variations
		Map<String, Map<String, String>> colorsAndSizesWithQuantity = new HashMap<>();
		List<ItemVariation> variations = orderItem.getVariations();
		if (variations != null && !variations.isEmpty()) {
			for (ItemVariation variation : variations) {
				String color = String.valueOf(variation.getColor());
				String size = String.valueOf(variation.getSize());
				Integer quantity = variation.getQuantity();

				// If color already exists, update its map with the new size and quantity; otherwise, create a new map for the color
				if (colorsAndSizesWithQuantity.containsKey(color)) {
					Map<String, String> sizeQuantityMap = colorsAndSizesWithQuantity.get(color);
					sizeQuantityMap.put("size", size);
					sizeQuantityMap.put("quantity", String.valueOf(quantity));
				} else {
					Map<String, String> sizeQuantityMap = new HashMap<>();
					sizeQuantityMap.put("size", size);
					sizeQuantityMap.put("quantity", String.valueOf(quantity));
					colorsAndSizesWithQuantity.put(color, sizeQuantityMap);
				}
			}
		}



		// Set the colorsAndSizesWithQuantity in the builder
		orderItemDtoBuilder.colorsAndSizesWithQuantity(colorsAndSizesWithQuantity);
		orderItemDtoBuilder.cartId(orderItem.getCartId());
		orderItemDtoBuilder.orderId(orderItem.getOrderId());
		orderItemDtoBuilder.product(ProductsItemDto.builder().productId(orderItem.getProduct().getProductId())
						.productTitle(orderItem.getProduct().getProductTitle())
						.discountPercent(orderItem.getProduct().getDiscountPercent())
						.imageUrl(orderItem.getProduct().getImageUrl())
						.price(orderItem.getProduct().getPrice())
				.build());
		return orderItemDtoBuilder.build();
	}

	public static OrderItem map(final OrderItemDto itemOrderDto, List<OrderItem> itemOrderList) {
		// Check if an item order with the same product ID already exists
		Optional<OrderItem> existingItemOrder = itemOrderList.stream()
				.filter(io -> io.getProduct().getProductId().equals(itemOrderDto.getProductId()))
				.findFirst();

		if (existingItemOrder.isPresent()) {
			// If the item order already exists, update its details and variations
			OrderItem itemOrder = existingItemOrder.get();
			int existingQuantity = itemOrder.getOrderedQuantity();
			itemOrder.setOrderedQuantity(existingQuantity + itemOrderDto.getOrderedQuantity());

			// Check if a variation with the same size and color already exists
			OrderItemServiceImpl.getExistingVariation(itemOrder, itemOrderDto);

			return itemOrder;
		} else {
			// If the item order does not exist, create a new one
			OrderItem itemOrder = OrderItem.builder()
					.product(
							Product.builder()
									.productId(itemOrderDto.getProductId())
									.build()
					)
					.orderId(itemOrderDto.getOrderId())
					.orderedQuantity(itemOrderDto.getOrderedQuantity())
					.totalPrice(itemOrderDto.getTotalPrice())
					.cartId(itemOrderDto.getCartId())
					.orderNow(itemOrderDto.isOrderNow())
					.build();

			// Create a list to hold variations
			List<ItemVariation> variations = new ArrayList<>();

			// Create an ItemVariation object
			ItemVariation variation = ItemVariation.builder()
					.color(Color.valueOf(itemOrderDto.getColor()))
					.size(Size.valueOf(itemOrderDto.getSize()))
					.quantity(itemOrderDto.getOrderedQuantity())  // Assuming initial quantity matches ordered quantity
					.orderItem(itemOrder)
					.product(Product.builder().productId(itemOrderDto.getProductId()).build())
					.build();

			variations.add(variation);

			// Set the variations list to the item order
			itemOrder.setVariations(variations);

			return itemOrder;
		}
	}

}









