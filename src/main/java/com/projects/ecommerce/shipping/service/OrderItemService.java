package com.projects.ecommerce.shipping.service;


import com.projects.ecommerce.shipping.domain.OrderItem;
import com.projects.ecommerce.shipping.domain.id.OrderItemId;
import com.projects.ecommerce.shipping.dto.OrderItemDto;

import java.util.Collection;
import java.util.List;

public interface OrderItemService {
	
	List<OrderItemDto> findAll();
	OrderItemDto findById(final OrderItemId orderItemId);
	OrderItemDto save(final OrderItemDto orderItemDto);
	OrderItemDto update(final OrderItemDto orderItemDto);
	void deleteById(final OrderItemId orderItemId);

	List<OrderItem> findAllByOrderId(Integer orderId);

	Collection<Object> findAllByCartId(Integer cartId);
}
