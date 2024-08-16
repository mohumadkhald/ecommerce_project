package com.projects.ecommerce.order;



import com.projects.ecommerce.product.domain.Color;
import com.projects.ecommerce.product.domain.ProductVariation;
import com.projects.ecommerce.product.domain.Size;
import com.projects.ecommerce.user.model.User;

import java.util.stream.Collectors;

public interface OrderMappingHelper {

	static OrderDto map(final Order order) {
		if (order == null) {
			return null;
		}

		return OrderDto.builder()
				.id(order.getId())
				.userId(order.getUser().getId())
				.orderItems(order.getOrderItems().stream()
						.map(OrderMappingHelper::mapOrderItem)
						.collect(Collectors.toList()))
				.totalPrice(order.getTotalPrice())
				.status(order.getStatus().name())
//				.paymentInfo(map(order.getPaymentInfo()))
				.shippingAddress(map(order.getShippingAddress()))
				.orderDate(order.getOrderDate())
				.deliveryDate(order.getDeliveryDate())
				.build();
	}

	static Order map(final OrderDto orderDto) {
		if (orderDto == null) {
			return null;
		}

		Order order = Order.builder()
				.id(orderDto.getId())
				.user(User.builder().id(orderDto.getUserId()).build()) // Assuming User is fetched elsewhere or use userId for later fetching
				.orderItems(orderDto.getOrderItems().stream()
						.map(OrderMappingHelper::mapOrderItem)
						.collect(Collectors.toList()))
				.totalPrice(orderDto.getTotalPrice())
				.status(OrderStatus.valueOf(orderDto.getStatus()))
				.paymentInfo(map(orderDto.getPaymentInfo()))
				.shippingAddress(map(orderDto.getShippingAddress()))
				.orderDate(orderDto.getOrderDate())
				.deliveryDate(orderDto.getDeliveryDate())
				.build();

		// Set the order reference for each order item
		order.getOrderItems().forEach(orderItem -> orderItem.setOrder(order));

		return order;
	}

	static OrderItemDto mapOrderItem(final OrderItem orderItem) {
		if (orderItem == null) {
			return null;
		}

		return OrderItemDto.builder()
				.productVariation(ProductVariationDto.builder()
						.color(String.valueOf(orderItem.getProductVariation().getColor()))
						.size(String.valueOf(orderItem.getProductVariation().getSize()))
						.quantity(orderItem.getQuantity())
						.build())
				.productName(orderItem.getProductVariation().getProduct().getProductTitle()) // Assuming there's a getProduct() in ProductVariation
				.img(orderItem.getProductVariation().getProduct().getImageUrl()) // Assuming there's a getProduct() in ProductVariation
				.price(orderItem.getProductVariation().getProduct().getPrice())
				.build();
	}

	static OrderItem mapOrderItem(final OrderItemDto orderItemDto) {
		if (orderItemDto == null) {
			return null;
		}

		return OrderItem.builder()
				.productVariation(ProductVariation.builder()
						.size(Size.valueOf(orderItemDto.getProductVariation().getSize()))
						.color(Color.valueOf(orderItemDto.getProductVariation().getColor()))
						.quantity(orderItemDto.getProductVariation().getQuantity())
						.build()) // Assuming ProductVariation is fetched elsewhere
				.price(orderItemDto.getPrice())
				.build();
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
}










