package com.projects.ecommerce.service;

import com.projects.ecommerce.exception.CartItemException;
import com.projects.ecommerce.exception.ProductException;
import com.projects.ecommerce.exception.UserException;
import com.projects.ecommerce.model.Cart;
import com.projects.ecommerce.model.CartItem;
import com.projects.ecommerce.model.Product;
import com.projects.ecommerce.requests.AddItemRequest;

public interface CartItemService {
    CartItem createCartItem(AddItemRequest cartItem) throws ProductException, UserException;

    CartItem updateCart(Long userID, Long id, CartItem cartItem) throws CartItemException, UserException;

    void removeCartItem(Long userId, Long cartItemId) throws CartItemException, UserException;


    CartItem findCartItemById(Long userId, Long cartItemId) throws CartItemException;
}
