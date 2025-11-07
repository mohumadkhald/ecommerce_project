package com.projects.ecommerce.order.helper;



import com.projects.ecommerce.order.dto.*;
import com.projects.ecommerce.order.model.*;
import com.projects.ecommerce.product.domain.Color;
import com.projects.ecommerce.product.domain.ProductVariation;
import com.projects.ecommerce.product.domain.Size;
import com.projects.ecommerce.user.dto.UserDto;
import com.projects.ecommerce.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface OrderMappingHelper {

	static OrderDto map(final Order order) {
		if (order == null) {
			return null;
		}

		// Group order items by product title
		Map<String, List<OrderItem>> groupedItems = order.getOrderItems().stream()
				.collect(Collectors.groupingBy(item -> item.getOrderVariation().getProductTitle()));

		// Map each group to an OrderItemDto with multiple ProductVariationDto objects
		List<OrderItemDto> orderItemDtos = groupedItems.entrySet().stream()
				.map(entry -> {
					List<ProductVariationDto> variations = entry.getValue().stream()
							.map(orderItem -> ProductVariationDto.builder()
									.color(String.valueOf(orderItem.getOrderVariation().getColor()))
									.size(String.valueOf(orderItem.getOrderVariation().getSize()))
									.quantity(orderItem.getQuantity())
									.build())
							.collect(Collectors.toList());

					OrderItem firstItem = entry.getValue().get(0);

					return OrderItemDto.builder()
							.productVariations(variations)
							.productName(firstItem.getOrderVariation().getProductTitle())
							.img(firstItem.getOrderVariation().getImg())
							.price(firstItem.getOrderVariation().getPrice())
							.discount(firstItem.getOrderVariation().getDiscountPercent())
							.totalPrice(firstItem.getOrderVariation().getDiscountedPrice())
							.build();
				})
				.collect(Collectors.toList());

		return OrderDto.builder()
				.id(order.getId())
				.userId(order.getUser().getId())
				.orderItems(orderItemDtos)
				.totalPrice(order.getTotalPrice())
				.status(order.getStatus().name())
				.shippingAddress(map(order.getShippingAddress()))
				.orderDate(order.getOrderDate())
				.deliveryDate(order.getDeliveryDate())
				.build();
	}

	static Order map(final OrderDto orderDto) {
		if (orderDto == null) {
			return null;
		}

		// Create the Order object
		Order order = Order.builder()
				.id(orderDto.getId())
				.user(User.builder().id(orderDto.getUserId()).build()) // Assuming User is referenced by id
				.totalPrice(orderDto.getTotalPrice())
				.status(OrderStatus.valueOf(orderDto.getStatus()))
				.paymentInfo(map(orderDto.getPaymentInfo()))
				.shippingAddress(map(orderDto.getShippingAddress()))
				.orderDate(orderDto.getOrderDate())
				.deliveryDate(orderDto.getDeliveryDate())
				.build();

		// Map and set OrderItems
		List<OrderItem> orderItems = orderDto.getOrderItems().stream()
				.flatMap(orderItemDto -> OrderMappingHelper.mapOrderItem(orderItemDto).stream()) // Handles multiple items from a single DTO
				.collect(Collectors.toList());

		// Set the order reference for each order item
		orderItems.forEach(orderItem -> orderItem.setOrder(order));

		// Add items to the order
		order.setOrderItems(orderItems);

		return order;
	}

	static OrderItemDto mapOrderItem(final List<OrderItem> orderItems) {
		if (orderItems == null || orderItems.isEmpty()) {
			return null;
		}

		// Extract product name and image from the first item (assuming they are the same for all variations)
		String productName = orderItems.get(0).getOrderVariation().getProductTitle();
		String img = orderItems.get(0).getOrderVariation().getImageUrl();
		Double price = orderItems.get(0).getOrderVariation().getPrice();

		// Map each order item to a ProductVariationDto
		List<ProductVariationDto> variations = orderItems.stream()
				.map(orderItem -> ProductVariationDto.builder()
						.color(String.valueOf(orderItem.getOrderVariation().getColor()))
						.size(String.valueOf(orderItem.getOrderVariation().getSize()))
						.quantity(orderItem.getQuantity())
						.build())
				.collect(Collectors.toList());

		// Return a single OrderItemDto containing all variations
		return OrderItemDto.builder()
				.productVariations(variations)
				.productName(productName)
				.img(img)
				.price(price)
				.build();
	}

	static List<OrderItem> mapOrderItem(final OrderItemDto orderItemDto) {
		if (orderItemDto == null || orderItemDto.getProductVariations() == null) {
			return Collections.emptyList();
		}

		return orderItemDto.getProductVariations().stream()
				.map(variation -> OrderItem.builder()
						.orderVariation(OrderVariation.builder()
								.size(variation.getSize())
								.color(variation.getColor())
								.quantity(variation.getQuantity())
								.build())
						.price(orderItemDto.getPrice())
						.build())
				.collect(Collectors.toList());
	}

	static PaymentInfoDto map(final PaymentInfo paymentInfo) {
		if (paymentInfo == null) {
			return null;
		}

		return PaymentInfoDto.builder()
				.cardHolderName(paymentInfo.getCardHolderName())
				.cardNumber(paymentInfo.getCardNumber()) // Consider masking or handling securely
				.expirationDate(paymentInfo.getExpirationDate())
				.cvv(paymentInfo.getCvv())
				.build();
	}

	static PaymentInfo map(final PaymentInfoDto paymentInfoDto) {
		if (paymentInfoDto == null) {
			return null;
		}

		return PaymentInfo.builder()
				.cardHolderName(paymentInfoDto.getCardHolderName())
				.cardNumber(paymentInfoDto.getCardNumber()) // Ensure to handle securely
				.expirationDate(paymentInfoDto.getExpirationDate())
				.cvv(paymentInfoDto.getCvv())
				.build();
	}

	static AddressDto map(final Address address) {
		if (address == null) {
			return null;
		}

		return AddressDto.builder()
				.street(address.getStreet())
				.city(address.getCity())
				.state(address.getState())
				.postalCode(address.getPostalCode())
				.country(address.getCountry())
				.build();
	}

	static Address map(final AddressDto addressDto) {
		if (addressDto == null) {
			return null;
		}

		return Address.builder()
				.street(addressDto.getStreet())
				.city(addressDto.getCity())
				.state(addressDto.getState())
				.postalCode(addressDto.getPostalCode())
				.country(addressDto.getCountry())
				.build();
	}

	static ProductVariation map(final ProductVariationDto productVariationDto) {
		if (productVariationDto == null) {
			return null;
		}
		return ProductVariation.builder()
				.color(Color.valueOf(productVariationDto.getColor()))
				.size(Size.valueOf(productVariationDto.getSize()))
				.build();
	}

	static ProductVariationDto map(final ProductVariation productVariation) {
		if (productVariation == null) {
			return null;
		}

		return ProductVariationDto.builder()
				.color(String.valueOf(productVariation.getColor()))
				.size(String.valueOf(productVariation.getSize()))
				.build();
	}

	static OrderDtoAdmin map1(final Order order) {
		if (order == null) {
			return null;
		}

		// Group order items by product title
		Map<String, List<OrderItem>> groupedItems = order.getOrderItems().stream()
				.collect(Collectors.groupingBy(item -> item.getOrderVariation().getProductTitle()));

		// Map each group to an OrderItemDto with multiple ProductVariationDto objects
		List<OrderItemDto> orderItemDtos = groupedItems.entrySet().stream()
				.map(entry -> {
					List<ProductVariationDto> variations = entry.getValue().stream()
							.map(orderItem -> ProductVariationDto.builder()
									.color(String.valueOf(orderItem.getOrderVariation().getColor()))
									.size(String.valueOf(orderItem.getOrderVariation().getSize()))
									.quantity(orderItem.getQuantity())
									.build())
							.collect(Collectors.toList());

					OrderItem firstItem = entry.getValue().get(0);

					return OrderItemDto.builder()
							.productVariations(variations)
							.productName(firstItem.getOrderVariation().getProductTitle())
							.img(firstItem.getOrderVariation().getImg())
							.price(firstItem.getOrderVariation().getPrice())
							.discount(firstItem.getOrderVariation().getDiscountPercent())
							.totalPrice(firstItem.getOrderVariation().getDiscountedPrice() * firstItem.getQuantity())
							.build();
				})
				.collect(Collectors.toList());

		return OrderDtoAdmin.builder()
				.id(order.getId())
				.user(map(order.getUser()))
				.orderItems(orderItemDtos)
				.totalPrice(order.getTotalPrice())
				.status(order.getStatus().name())
				.paymentInfo(map(order.getPaymentInfo()))
				.shippingAddress(map(order.getShippingAddress()))
				.orderDate(order.getOrderDate())
				.deliveryDate(order.getDeliveryDate())
				.build();
	}

	static UserDto map(User user) {
		if (user == null) {
			return null;
		}
		return  UserDto.builder()
				.userId(user.getId())
				.email(user.getEmail())
				.firstName(user.getFirstname())
				.lastName(user.getLastname())
				.gender(user.getGender())
				.imageUrl(user.getImgUrl())
				.emailVerified(user.getEmailVerification().isEmailVerified())
				.role(user.getRole().toString())
				.build();
	}

}










