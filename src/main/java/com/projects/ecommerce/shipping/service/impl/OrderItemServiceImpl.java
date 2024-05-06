package com.projects.ecommerce.shipping.service.impl;


import com.projects.ecommerce.product.domain.Product;
import com.projects.ecommerce.product.dto.Spec;
import com.projects.ecommerce.product.service.ProductService;
import com.projects.ecommerce.shipping.domain.OrderItem;
import com.projects.ecommerce.shipping.domain.id.OrderItemId;
import com.projects.ecommerce.shipping.dto.OrderItemDto;
import com.projects.ecommerce.shipping.exception.wrapper.OrderItemNotFoundException;
import com.projects.ecommerce.shipping.helper.OrderItemMappingHelper;
import com.projects.ecommerce.shipping.repository.OrderItemRepository;
import com.projects.ecommerce.shipping.service.OrderItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
	
	private final OrderItemRepository orderItemRepository;
	private final ProductService productService;

	@Override
	public List<OrderItemDto> findAll() {
		log.info("*** OrderItemDto List, service; fetch all orderItems *");
		return this.orderItemRepository.findAll()
				.stream()
					.map(OrderItemMappingHelper::map)

					.distinct()
					.toList();
	}
	
	@Override
	public OrderItemDto findById(final OrderItemId orderItemId) {
		log.info("*** OrderItemDto, service; fetch orderItem by id *");
		return this.orderItemRepository.findById(orderItemId)
				.map(OrderItemMappingHelper::map)
				.orElseThrow(() -> new OrderItemNotFoundException(String.format("OrderItem with id: %s not found", orderItemId)));
	}




	@Override
	public OrderItemDto save(final OrderItemDto orderItemDto) {
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

		// Save order item
		return OrderItemMappingHelper.map(this.orderItemRepository.save(OrderItemMappingHelper.map(orderItemDto)));
	}

	
	@Override
	public OrderItemDto update(final OrderItemDto orderItemDto) {
		log.info("*** OrderItemDto, service; update orderItem *");
		return OrderItemMappingHelper.map(this.orderItemRepository
				.save(OrderItemMappingHelper.map(orderItemDto)));
	}
	
	@Override
	public void deleteById(final OrderItemId orderItemId) {
		log.info("*** Void, service; delete orderItem by id *");
		this.orderItemRepository.deleteById(orderItemId);
	}

	@Override
	public List<OrderItem> findAllByOrderId(Integer orderId) {
		return orderItemRepository.findByOrderId(orderId);
	}

	@Override
	public Collection<Object> findAllByCartId(Integer cartId) {
		return this.orderItemRepository.findAllByCartId(cartId);
	}


}









