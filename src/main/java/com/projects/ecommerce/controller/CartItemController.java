package com.projects.ecommerce.controller;

import com.projects.ecommerce.exception.CartItemException;
import com.projects.ecommerce.exception.ProductException;
import com.projects.ecommerce.exception.UserException;
import com.projects.ecommerce.model.CartItem;
import com.projects.ecommerce.requests.AddItemRequest;
import com.projects.ecommerce.service.CartItemService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/cart-items")
public class CartItemController {

    private CartItemService cartItemService;

    @PostMapping("/cart-items")
    public ResponseEntity<CartItem> createCartItem(@RequestBody AddItemRequest addItemRequest, @RequestHeader("Authorization") String jwtToken) {
        try {
            CartItem cartItem = cartItemService.createCartItem(addItemRequest, jwtToken);
            return new ResponseEntity<>(cartItem, HttpStatus.CREATED);
        } catch (ProductException | UserException e) {
            // Handle exceptions
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{userId}/{id}")
    public ResponseEntity<CartItem> updateCartItem(@PathVariable Long userId, @PathVariable Long id, @RequestBody CartItem cartItem) {
        try {
            CartItem updatedCartItem = cartItemService.updateCart(userId, id, cartItem);
            return ResponseEntity.ok(updatedCartItem);
        } catch (CartItemException | UserException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{userId}/{cartItemId}")
    public ResponseEntity<?> removeCartItem(@PathVariable Long userId, @PathVariable Long cartItemId) {
        try {
            cartItemService.removeCartItem(userId, cartItemId);
            return ResponseEntity.noContent().build();
        } catch (CartItemException | UserException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/{userId}/{cartItemId}")
    public ResponseEntity<CartItem> findCartItemById(@PathVariable Long userId, @PathVariable Long cartItemId) {
        try {
            CartItem cartItem = cartItemService.findCartItemById(userId, cartItemId);
            return ResponseEntity.ok(cartItem);
        } catch (CartItemException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
