package com.projects.ecommerce.shipping.service;


import com.projects.ecommerce.shipping.domain.OrderItem;
import com.projects.ecommerce.shipping.dto.OrderItemDto;
import com.projects.ecommerce.shipping.dto.OrderItemsDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderItemService {

	OrderItemsDto create(OrderItemDto orderItemDto);

	List<OrderItemsDto> saveAll(List<OrderItemDto> orderItemDtos);

	List<OrderItemsDto> findAll();
//	OrderItemDto save(final OrderItemDto orderItemDto);
	OrderItemsDto update(final OrderItemDto orderItemDto);

//	OrderItemsDto save(OrderItemsDto orderItemDto);

	OrderItemsDto save(OrderItemDto orderItemDto);

	void deleteById(final Integer orderItemId);

	List<OrderItem> findAllByOrderId(Integer orderId);

	List<OrderItemsDto> findAllByCartId(Integer cartId);



	ResponseEntity<String> deletOrderById(Integer orderItemId);
}
