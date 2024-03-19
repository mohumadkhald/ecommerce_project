package com.projects.ecommerce.service;

import com.projects.ecommerce.config.JwtProvider;
import com.projects.ecommerce.exception.CartItemException;
import com.projects.ecommerce.exception.ProductException;
import com.projects.ecommerce.exception.UserException;
import com.projects.ecommerce.model.Cart;
import com.projects.ecommerce.model.CartItem;
import com.projects.ecommerce.model.User;
import com.projects.ecommerce.repo.CartItemRepo;
import com.projects.ecommerce.requests.AddItemRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    
    private CartItemRepo cartItemRepo;
    private ProductService productService;
    private UserService userService;
    private JwtProvider jwtProvider;

    @Override
    public CartItem createCartItem(AddItemRequest addItemRequest, String jwtToken) throws ProductException, UserException {
        // Get user ID from JWT
        Long userId = userService.findUserIdByJwt(jwtToken);

        // Retrieve the user by their ID
        User user = userService.findUserById(userId);
        if (user == null) {
            // Handle the case where user does not exist
            throw new UserException("User not found");
        }

        // Get the cart associated with the user (assuming a user has one cart)
        Cart cart = user.getCart();
        if (cart == null) {
            // Handle the case where the cart for the user does not exist
            throw new UserException("Cart not found for the user");
        }

        // Create a new cart item
        CartItem cartItem = new CartItem();

        // Set the fields from the AddItemRequest
        cartItem.setProduct(productService.findProductById(addItemRequest.getProductId()));
        cartItem.setSize(addItemRequest.getSize());
        cartItem.setQuantity(addItemRequest.getQuantity());
        cartItem.setPrice(addItemRequest.getPrice());

        // Set user ID
        cartItem.setUserId(userId);
        Cart cart1 = new Cart();
        cart1.setId(userId);
        // Set the cart associated with the user
        cartItem.setCart(cart1);
        // Save the cart item in the repository
        return cartItemRepo.save(cartItem);
    }







    @Override
    public CartItem updateCart(Long userId, Long id, CartItem cartItem) throws CartItemException, UserException {
        // Fetch the user's cart item by ID
        CartItem existingCartItem = cartItemRepo.findById(id)
                .orElseThrow(() -> new CartItemException("Cart item not found"));

        // Check if the user ID matches the user ID associated with the cart item
        if (!existingCartItem.getUserId().equals(userId)) {
            throw new UserException("Unauthorized access to cart item");
        }

        // Update the fields of the existing cart item
        existingCartItem.setSize(cartItem.getSize());
        existingCartItem.setQuantity(cartItem.getQuantity());
        existingCartItem.setPrice(cartItem.getPrice());

        // Save the updated cart item in the repository
        return cartItemRepo.save(existingCartItem);
    }


    @Override
    public void removeCartItem(Long userId, Long cartItemId) throws CartItemException, UserException {
        // Check if the cart item exists
        CartItem cartItem = cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new CartItemException("Cart item not found"));

        // Check if the user ID associated with the cart item matches the provided user ID
        if (!cartItem.getUserId().equals(userId)) {
            throw new UserException("Unauthorized access to cart item");
        }

        // Delete the cart item
        cartItemRepo.delete(cartItem);
    }

    @Override
    public CartItem findCartItemById(Long userId, Long cartItemId) throws CartItemException {
        // Fetch the cart item by its ID
        CartItem cartItem = cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new CartItemException("Cart item not found"));

        // Check if the cart item belongs to the specified user
        if (!cartItem.getUserId().equals(userId)) {
            throw new CartItemException("Cart item does not belong to the specified user");
        }

        return cartItem;
    }
}
