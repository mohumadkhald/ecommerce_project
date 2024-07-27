package com.projects.ecommerce.cart;

import com.projects.ecommerce.product.domain.Color;
import com.projects.ecommerce.product.domain.Product;
import com.projects.ecommerce.product.domain.ProductVariation;
import com.projects.ecommerce.product.domain.Size;
import com.projects.ecommerce.product.service.ProdcutVariationService;
import com.projects.ecommerce.product.service.impl.ProductServiceImpl;
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
    @Autowired
    private ProductServiceImpl productServiceImpl;

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
            Product product = productServiceImpl.findProductById(cartRequest.getProductId());
            ProductVariation newProductVariation = new ProductVariation();
            newProductVariation.setColor(Color.valueOf(cartRequest.getColor()));
            newProductVariation.setSize(Size.valueOf(cartRequest.getSize()));
            newProductVariation.setQuantity(0);
            newProductVariation.setProduct(
                    product
            );
            prodcutVariationService.save(newProductVariation);
            productVariation = newProductVariation;
        }
        // Check if the cart already contains this product variation
        ProductVariation finalProductVariation = productVariation;
        Optional<CartItem> existingCartItem = cart.getItems().stream()
                .filter(item -> item.getProductVariation().getId().equals(finalProductVariation.getId()))
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
        // Retrieve user cart from the database
        Cart cart = getCartByUserId(userId);

        // Create or update cart items based on the data received from the client
        List<CartItem> newCartItems = new ArrayList<>();

        for (CartRequest dto : cartItems) {
            ProductVariation productVariation = prodcutVariationService.findByProductIdAndColorAndSize(
                    dto.getProductId(),
                    Color.valueOf(dto.getColor()),
                    dto.getSize()
            );

            if (productVariation == null) {
                throw new RuntimeException("Product variation not found");
            }

            // Check if the cart already contains this product variation
            Optional<CartItem> existingCartItem = cart.getItems().stream()
                    .filter(item -> item.getProductVariation().getId().equals(productVariation.getId()))
                    .findFirst();

            if (existingCartItem.isPresent()) {
                // Update the quantity and price of the existing cart item
                CartItem cartItem = existingCartItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + dto.getQuantity());
                cartItem.setPrice(cartItem.getQuantity() * dto.getPrice());
                newCartItems.add(cartItem);
            } else {
                // Create a new cart item
                CartItem cartItem = new CartItem();
                cartItem.setCart(cart);
                cartItem.setPrice(dto.getQuantity() * dto.getPrice());
                cartItem.setQuantity(dto.getQuantity());
                cartItem.setProductVariation(productVariation);
                newCartItems.add(cartItem);
            }
        }

        // Save new or updated cart items to the database
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
