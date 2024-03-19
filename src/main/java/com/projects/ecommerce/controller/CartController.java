package com.projects.ecommerce.controller;

import com.projects.ecommerce.exception.UserException;
import com.projects.ecommerce.model.Cart;
import com.projects.ecommerce.model.CartItem;
import com.projects.ecommerce.model.Product;
import com.projects.ecommerce.model.User;
import com.projects.ecommerce.service.CartService;
import com.projects.ecommerce.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private CartService cartService;
    private UserService userService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<Cart> createCart(@PathVariable Long userId, @RequestBody Set<Long> cartItemIds) throws UserException {
        // Retrieve the user by userId
        User user = userService.findUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Retrieve the cart items by their IDs
        Set<CartItem> cartItems = cartService.findCartItemsByIds(cartItemIds);

        // Create the cart with the user and cart items
        Cart createdCart = cartService.createCart(user, cartItems);

        return new ResponseEntity<>(createdCart, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getUserCart(@PathVariable Long userId) {
        Cart userCart = cartService.findUserCart(userId);
        return ResponseEntity.ok(userCart);
    }

    @DeleteMapping("/")
    public ResponseEntity<String> clearCart(@RequestBody User user) {
        cartService.clearCart(user);
        return ResponseEntity.ok("Cart cleared successfully");
    }

    @GetMapping("/products/{userId}")
    public ResponseEntity<List<Product>> getAllProductsInCartByUserId(@PathVariable Long userId) {
        List<Product> productsInCart = cartService.getAllProductsInCartByUserId(userId);
        if (!productsInCart.isEmpty()) {
            return ResponseEntity.ok(productsInCart);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
