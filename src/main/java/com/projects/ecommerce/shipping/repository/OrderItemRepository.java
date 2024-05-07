package com.projects.ecommerce.shipping.repository;


import com.projects.ecommerce.shipping.domain.OrderItem;
import com.projects.ecommerce.shipping.domain.id.OrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {



    List<OrderItem> findByOrderId(Integer orderId);

    List<OrderItem> findAllByCartId(Integer cartId);


}
