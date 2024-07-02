package com.projects.ecommerce.cart;

import com.projects.ecommerce.product.domain.Color;
import com.projects.ecommerce.product.domain.Product;
import com.projects.ecommerce.product.domain.ProductVariation;
import com.projects.ecommerce.product.service.ProdcutVariationService;
import com.projects.ecommerce.user.model.User;
import com.projects.ecommerce.user.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CartService {

    private CartRepository cartRepository;

    private CartItemRepository cartItemRepository;

    private ProdcutVariationService prodcutVariationService;

    @Autowired
    private UserRepo userRepository;

    public Cart getCartByUserId(Integer userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCartForUser(userId));
    }

    private Cart createNewCartForUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = Cart.builder()
                .user(user)
                .items(new ArrayList<>())
                .build();

        return cartRepository.save(cart);
    }

    public CartItemDto addProductToCart(Integer userId, CartRequest cartRequest) {
        Cart cart = getCartByUserId(userId);
        CartItem cartItem = new CartItem();
        ProductVariation productVariation = prodcutVariationService.findByProductIdAndColorAndSize(
                cartRequest.getProductId(),
                Color.valueOf(cartRequest.getColor()),
                cartRequest.getSize()
        );
        if (productVariation == null) {
            throw new RuntimeException("Product variation not found");
        }
        // Check if the cart already contains this product variation
        Optional<CartItem> existingCartItem = cart.getItems().stream()
                .filter(item -> item.getProductVariation().getId().equals(productVariation.getId()))
                .findFirst();

        if (existingCartItem.isPresent())
        {
            CartItem cartItem1 = existingCartItem.get();
            cartItem1.setQuantity(existingCartItem.get().getQuantity() + cartRequest.getQuantity());
            cartItem1.setPrice(existingCartItem.get().getPrice() + cartRequest.getQuantity() * productVariation.getProduct().getPrice());
            cartItem = cartItem1;
        } else {
            cartItem.setQuantity(cartRequest.getQuantity());
            cartItem.setCart(cart);
            cartItem.setProductVariation(productVariation);
            Product product = productVariation.getProduct();
            cartItem.setPrice(product.getPrice() * cartRequest.getQuantity());
        }

        return CartItemMappingHelper.map(cartItemRepository.save(cartItem));
    }

    public void removeItemFromCart(Integer userId, Integer itemId) {
        Cart cart = getCartByUserId(userId);

        // Remove the item with the matching itemId
        cart.getItems().removeIf(item -> item.getId().equals(itemId));

        // Save the updated cart
        cartRepository.save(cart);
    }

    public Double calculateTotal(Integer userId) {
        Cart cart = getCartByUserId(userId);
        return cart.getItems().stream()
                .mapToDouble(CartItem::getPrice)
                .sum();
    }

    public List<CartItemDto> getAllItem(Integer userId) {
        Cart cart = getCartByUserId(userId);

        return cart.getItems().stream()
                .map(CartItemMappingHelper::map)
                .collect(Collectors.toList());
    }


    public void syncCart(Integer userId, List<CartRequest> cartItems) {
        // Retrieve user from database
        Cart cart = getCartByUserId(userId);


        // Create new cart items based on the data received from the client
        List<CartItem> newCartItems = cartItems.stream()
                .map(dto -> {
                    CartItem cartItem = new CartItem();
                    cartItem.setCart(cart);
                    cartItem.setPrice(dto.getQuantity() * dto.getPrice());
                    cartItem.setQuantity(dto.getQuantity());
                    cartItem.setProductVariation(prodcutVariationService
                            .findByProductIdAndColorAndSize(
                                    dto.getProductId(),
                                    Color.valueOf(dto.getColor()),
                                    dto.getSize())
                    );
                    return cartItem;
                })
                .collect(Collectors.toList());

        // Save new cart items to the database
        cartItemRepository.saveAll(newCartItems);
    }

    public void removeAllItemsFromCart(Integer userId) {
        // Retrieve the cart associated with the given user ID
        Cart cart = getCartByUserId(userId);

        if (cart != null) {
            // Clear all items from the cart
            cart.getItems().clear();

            // Save the updated cart
            cartRepository.save(cart);
        } else {
            throw new IllegalArgumentException("Cart not found for user ID: " + userId);
        }
    }

}
