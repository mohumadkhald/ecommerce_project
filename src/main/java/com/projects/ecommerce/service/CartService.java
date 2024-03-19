package com.projects.ecommerce.service;

import com.projects.ecommerce.exception.ProductException;
import com.projects.ecommerce.model.Cart;
import com.projects.ecommerce.model.User;
import com.projects.ecommerce.requests.AddItemRequest;
import org.springframework.stereotype.Service;

@Service
public interface CartService {


    Cart createCart(User user);

    public String addCartItem(Long userid, AddItemRequest request) throws ProductException;

    public Cart findUserCart(Long userId);

    ////////////////////////////////////////////////////////////////////////

    Cart getUserCart(User user);

    void clearCart(User user);
}
