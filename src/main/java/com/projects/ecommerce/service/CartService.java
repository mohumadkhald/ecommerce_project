package com.projects.ecommerce.service;

import com.projects.ecommerce.exception.UserException;
import com.projects.ecommerce.model.Cart;
import com.projects.ecommerce.model.CartItem;
import com.projects.ecommerce.model.Product;
import com.projects.ecommerce.model.User;
import com.projects.ecommerce.requests.CartRequestDTO;

import java.util.List;
import java.util.Set;

public interface CartService {



    Cart findUserCart(Long userId);

    void clearCart(User user);

    Set<CartItem> findCartItemsByIds(Set<Long> cartItemIds);

    List<Product> getAllProductsInCartByUserId(Long userId);

    Cart createCart(CartRequestDTO cartRequestDTO, String jwtToken) throws UserException;
}
