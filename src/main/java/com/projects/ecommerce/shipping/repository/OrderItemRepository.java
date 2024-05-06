package com.projects.ecommerce.shipping.repository;


import com.projects.ecommerce.shipping.domain.OrderItem;
import com.projects.ecommerce.shipping.domain.id.OrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {



    List<OrderItem> findByOrderId(Integer orderId);

    Collection<Object> findAllByCartId(Integer cartId);
}
