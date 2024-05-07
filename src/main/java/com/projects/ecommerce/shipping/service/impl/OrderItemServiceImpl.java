package com.projects.ecommerce.shipping.service.impl;


import com.projects.ecommerce.product.domain.Color;
import com.projects.ecommerce.product.domain.Product;
import com.projects.ecommerce.product.domain.Size;
import com.projects.ecommerce.product.dto.Spec;
import com.projects.ecommerce.product.service.ProductService;
import com.projects.ecommerce.shipping.domain.ItemVariation;
import com.projects.ecommerce.shipping.domain.OrderItem;
import com.projects.ecommerce.shipping.dto.OrderItemDto;
import com.projects.ecommerce.shipping.dto.OrderItemsDto;
import com.projects.ecommerce.shipping.helper.OrderItemMappingHelper;
import com.projects.ecommerce.shipping.repository.OrderItemRepository;
import com.projects.ecommerce.shipping.service.OrderItemService;
import com.projects.ecommerce.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
	
	private final OrderItemRepository orderItemRepository;
	private final ProductService productService;

	@Override
	public OrderItemsDto create(final OrderItemDto orderItemDto) {
		log.info("*** OrderItemDto, service; save order item ***");

		// Check if the product's stock is sufficient
		Product product = productService.findProductById(orderItemDto.getProductId());
		if (product.getAllQuantity() < orderItemDto.getOrderedQuantity()) {
			log.info("OrderItemDto, product quantity is insufficient");
			throw new RuntimeException("OrderItemDto, product quantity is insufficient");
		}

		// Update product stock
		productService.updateProductStock(orderItemDto.getProductId(), Collections.emptyList(), orderItemDto.getOrderedQuantity());

		List<OrderItem> existingProducts = this.orderItemRepository.findAll();

		// Create order item
		OrderItem orderItem = OrderItemMappingHelper.map(orderItemDto, existingProducts);
		// Update variations
		updateVariations(orderItem, orderItemDto);

		// Calculate total price
		orderItemDto.setTotalPrice(product.getPrice() * orderItemDto.getOrderedQuantity());

		// Save order item
		return OrderItemMappingHelper.map(this.orderItemRepository.save(orderItem));
	}

	@Override
	public List<OrderItemsDto> saveAll(List<OrderItemDto> orderItemDtos) {
		log.info("*** OrderItemDto, service; save order items ***");
		// Check product availability and update stock for each order item
		for (OrderItemDto orderItemDto : orderItemDtos) {
			Product product = productService.findProductById(orderItemDto.getProductId());
			if (product.getAllQuantity() < orderItemDto.getOrderedQuantity()) {
				log.info("OrderItemDto, product quantity is insufficient");
				throw new RuntimeException("OrderItemDto, product quantity is insufficient");
			}
			productService.updateProductStock(orderItemDto.getProductId(), Collections.emptyList(), orderItemDto.getOrderedQuantity());
		}
		List<OrderItem> existingProducts = this.orderItemRepository.findAll();


		// Save all order items
		List<OrderItem> orderItems = orderItemDtos.stream()
				.map(orderItemDto -> {
					OrderItem orderItem = OrderItemMappingHelper.map(orderItemDto, existingProducts);
					// Update variations
					updateVariations(orderItem, orderItemDto);
					return orderItem;
				})
				.collect(Collectors.toList());

		List<OrderItem> savedOrderItems = this.orderItemRepository.saveAll(orderItems);

		return savedOrderItems.stream()
				.map(OrderItemMappingHelper::map)
				.collect(Collectors.toList());
	}

	private void updateVariations(OrderItem orderItem, OrderItemDto orderItemDto) {
		getExistingVariation(orderItem, orderItemDto);
	}
	public static void getExistingVariation(OrderItem orderItem, OrderItemDto orderItemDto) {
		Optional<ItemVariation> existingVariation = orderItem.getVariations().stream()
				.filter(v -> {
					Product product = v.getProduct();
					return product != null &&
							product.getProductId().equals(orderItemDto.getProductId()) &&
							v.getColor().equals(Color.valueOf(orderItemDto.getColor())) &&
							v.getSize().equals(Size.valueOf(orderItemDto.getSize()));
				})
				.findFirst();

		if (existingVariation.isPresent()) {
			// If the variation already exists, update its quantity
			ItemVariation variation = existingVariation.get();
			int variationQuantity = variation.getQuantity();
			variation.setQuantity(variationQuantity + orderItemDto.getOrderedQuantity());
		} else {
			// If the variation does not exist, create a new one
			ItemVariation newVariation = ItemVariation.builder()
					.color(Color.valueOf(orderItemDto.getColor()))
					.size(Size.valueOf(orderItemDto.getSize()))
					.quantity(orderItemDto.getOrderedQuantity())
					.orderItem(orderItem)
					.product(Product.builder().productId(orderItemDto.getProductId()).build())
					.build();

			orderItem.getVariations().add(newVariation);
		}

		// Update the total quantity of the order item
		int totalQuantity = orderItem.getVariations().stream()
				.mapToInt(ItemVariation::getQuantity)
				.sum();
		orderItem.setOrderedQuantity(totalQuantity);

		// Update the total price based on product price
		Product product = orderItem.getVariations().get(0).getProduct(); // Assuming variations are for the same product
		if (product != null) {
			double totalPrice = product.getPrice() * totalQuantity;
			orderItem.setTotalPrice(totalPrice);
		}
	}

//	public static void getExistingVariation(OrderItem orderItem, OrderItemDto orderItemDto) {
//		Optional<ItemVariation> existingVariation = orderItem.getVariations().stream()
//				.filter(v -> v.getProduct().getProductId().equals(orderItemDto.getProductId()) &&
//						v.getColor().equals(Color.valueOf(orderItemDto.getColor())) &&
//						v.getSize().equals(Size.valueOf(orderItemDto.getSize())))
//				.findFirst();
//
//		if (existingVariation.isPresent()) {
//			// If the variation already exists, update its quantity
//			ItemVariation variation = existingVariation.get();
//			int variationQuantity = variation.getQuantity();
//			variation.setQuantity(variationQuantity + orderItemDto.getOrderedQuantity());
//		} else {
//			// If the variation does not exist, create a new one
//			ItemVariation newVariation = ItemVariation.builder()
//					.color(Color.valueOf(orderItemDto.getColor()))
//					.size(Size.valueOf(orderItemDto.getSize()))
//					.quantity(orderItemDto.getOrderedQuantity())
//					.orderItem(orderItem)
//					.build();
//
//			orderItem.getVariations().add(newVariation);
//		}
//
//		// Update the total quantity of the order item
//		int totalQuantity = orderItem.getVariations().stream()
//				.mapToInt(ItemVariation::getQuantity)
//				.sum();
//		orderItem.setOrderedQuantity(totalQuantity);
//	}



	@Override
	public List<OrderItemsDto> findAll() {
		List<OrderItem> orderItems = orderItemRepository.findAll();
		List<OrderItemsDto> orderItemsDtos = new ArrayList<>();

		for (OrderItem orderItem : orderItems) {
			orderItemsDtos.add(OrderItemMappingHelper.map(orderItem));
		}

		return orderItemsDtos;
	}



	@Override
	public OrderItemsDto update(OrderItemDto orderItemDto) {
		return null;
	}

	@Override
	public OrderItemsDto save(final OrderItemDto orderItemDto) {
		log.info("*** OrderItemDto, service; save orderItem ***");
		Product product = productService.findProductById(orderItemDto.getProductId());

		// Check if the ordered quantity exceeds the available quantity
		if (product.getAllQuantity() < orderItemDto.getOrderedQuantity()) {
			log.info("OrderItemDto, product quantity is insufficient");
			throw new RuntimeException("OrderItemDto, product quantity is insufficient");
		}

		// Update product stock
		productService.updateProductStock(orderItemDto.getProductId(), Collections.singletonList(new Spec(orderItemDto.getColor(), orderItemDto.getSize(), orderItemDto.getOrderedQuantity())), orderItemDto.getOrderedQuantity());

		// Calculate total price
		orderItemDto.setTotalPrice(product.getPrice() * orderItemDto.getOrderedQuantity());

		List<OrderItem> existingProducts = this.orderItemRepository.findAll();

		// Save order item
		return OrderItemMappingHelper.map(this.orderItemRepository.save(OrderItemMappingHelper.map(orderItemDto, existingProducts)));
	}



//
//	@Override
//	public OrderItemDto update(final OrderItemDto orderItemDto) {
//		log.info("*** OrderItemDto, service; update orderItem *");
//		return OrderItemMappingHelper.map(this.orderItemRepository
//				.save(OrderItemMappingHelper.map(orderItemDto)));
//	}


//	@Override
//	public List<OrderItemDto> findAll() {
//		log.info("*** OrderItemDto List, service; fetch all orderItems *");
//		return this.orderItemRepository.findAll()
//				.stream()
//					.map(OrderItemMappingHelper::map)
//
//					.distinct()
//					.toList();
//	}

	//	@Override
//	public OrderItemDto findById(final OrderItemId orderItemId) {
//		log.info("*** OrderItemDto, service; fetch orderItem by id *");
//		return this.orderItemRepository.findById(orderItemId)
//				.map(OrderItemMappingHelper::map)
//				.orElseThrow(() -> new OrderItemNotFoundException(String.format("OrderItem with id: %s not found", orderItemId)));
//	}
	@Override
	public void deleteById(final Integer orderItemId) {
		log.info("*** Void, service; delete orderItem by id *");
//		this.orderItemRepository.removeOrderItemsByItem_id(orderItemId);
	}

	@Override
	public List<OrderItem> findAllByOrderId(Integer orderId) {
		return orderItemRepository.findByOrderId(orderId);
	}

	@Override
	public List<OrderItemsDto> findAllByCartId(Integer cartId) {
		List<OrderItem> orderItems = orderItemRepository.findAllByCartId(cartId);
		List<OrderItemsDto> orderItemsDtos = new ArrayList<>();

		for (OrderItem orderItem : orderItems) {
			orderItemsDtos.add(OrderItemMappingHelper.map(orderItem));
		}

		return orderItemsDtos;
	}

	@Override
	public ResponseEntity<String> deletOrderById(Integer orderItemId) {
		Optional<OrderItem> orderItem = orderItemRepository.findById(orderItemId);
		if (orderItem.isPresent())
		{
			orderItemRepository.deleteById(orderItemId);
			return ResponseEntity.ok("user deleted");
		}
		return ResponseEntity.ok("user not found");
	}




}









