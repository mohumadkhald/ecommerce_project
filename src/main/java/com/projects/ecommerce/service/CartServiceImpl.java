package com.projects.ecommerce.service;

import com.projects.ecommerce.model.Cart;
import com.projects.ecommerce.model.CartItem;
import com.projects.ecommerce.model.Product;
import com.projects.ecommerce.model.User;
import com.projects.ecommerce.repo.CartRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

    private CartRepo cartRepo;

    @Override
    public Cart createCart(User user, Set<CartItem> cartItems) {
        // Create a new cart
        Cart cart = new Cart();

        // Associate the user with the cart
        cart.setUser(user);

        // Set the cart items and associate them with the cart
        cart.setCartItems(cartItems);

        // Associate each cart item with the cart
        for (CartItem cartItem : cartItems) {
            cartItem.setCart(cart);
        }

        // Calculate total price, total item, total discounted price, and discount here
        // Assuming you have logic to calculate these values based on the cart items

        // Save the cart
        return cartRepo.save(cart);
    }


    @Override
    public Cart findUserCart(Long userId) {
        return cartRepo.findCartByUserId(userId);
    }

    @Override
    public void clearCart(User user) {
        Cart cart = cartRepo.findByUser(user);
        if (cart != null) {
            cartRepo.delete(cart);
        }
    }

    @Override
    public Set<CartItem> findCartItemsByIds(Set<Long> cartItemIds) {
        return null;
    }

    @Override
    public List<Product> getAllProductsInCartByUserId(Long userId) {
        // Assuming cartRepository has a method to find products by user ID
        List<Product> productsInCart = cartRepo.findProductsByUserId(userId);
        return productsInCart;
    }
}
