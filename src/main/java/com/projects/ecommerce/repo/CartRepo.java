package com.projects.ecommerce.repo;

import com.projects.ecommerce.model.Cart;
import com.projects.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartRepo extends JpaRepository<Cart, Long> {
    @Query("select  c from Cart c where c.user.id=:userId")
    public Cart findCartByUserId(@Param("userId") Long userId);

    Cart findByUser(User user);
}
