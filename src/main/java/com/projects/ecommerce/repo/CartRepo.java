package com.projects.ecommerce.repo;

import com.projects.ecommerce.model.Cart;
import com.projects.ecommerce.model.Product;
import com.projects.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartRepo extends JpaRepository<Cart, Long> {
    @Query("select c from Cart c where c.user.id = :userId")
    Cart findCartByUserId(@Param("userId") Long userId);

    Cart findByUser(User user);

    @Query("SELECT ci.product FROM CartItem ci WHERE ci.cart.user.id = :userId")
    List<Product> findProductsByUserId(@Param("userId") Long userId);}