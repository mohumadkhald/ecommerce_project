package com.projects.ecommerce.order.repository;

import com.projects.ecommerce.order.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Integer> {


    Cart findByUserId(Integer userId);


    Cart findCartByUserId(Integer userId);
}
