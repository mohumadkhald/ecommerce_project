package com.projects.ecommerce.service;

import com.projects.ecommerce.exception.ProductException;
import com.projects.ecommerce.model.Cart;
import com.projects.ecommerce.model.CartItem;
import com.projects.ecommerce.model.Product;
import com.projects.ecommerce.model.User;
import com.projects.ecommerce.repo.CartRepo;
import com.projects.ecommerce.requests.AddItemRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class CartServiceImplement implements CartService {
    private CartRepo cartRepo;
    private CartItemService cartItemService;
    private ProductService productService;


    @Override
    public Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepo.save(cart);
    }

    @Override
    public String addCartItem(Long userid, AddItemRequest request) throws ProductException {
        Cart cart = cartRepo.findCartByUserId(userid);
        Product product = productService.findProductById(request.getProductId());
        CartItem isPresent = cartItemService.isCartItemExist(cart, product, request.getSize(), userid);
        if (isPresent == null)
        {
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(cart);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setUserId(userid);

            int price = request.getQuantity()*product.getDiscountedPrice();
            cartItem.setPrice(price);
            cartItem.setSize(request.getSize());

            CartItem created = cartItemService.createCartItem(cartItem);
            cart.getCartItems().add(created);
        }
        return "item add to cart success";
    }

    @Override
    public Cart findUserCart(Long userId) {
        Cart cart = cartRepo.findCartByUserId(userId);

        int totalPrice = 0;
        int totalDiscountedPrice = 0;
        int totalItem = 0;

        for (CartItem cartItem : cart.getCartItems()){
            totalPrice = totalPrice + cartItem.getPrice();
            totalDiscountedPrice = totalDiscountedPrice + cartItem.getDiscountedPrice();
            totalItem = totalItem + cartItem.getQuantity();
        }

        cart.setTotalDiscountedPrice(totalDiscountedPrice);
        cart.setTotalItem(totalItem);
        cart.setTotalPrice(totalPrice);
        cart.setDiscount(totalPrice-totalDiscountedPrice);
        return cartRepo.save(cart);
    }

    // Constructor injection

    @Override
    public Cart getUserCart(User user) {
        // Retrieve the user's cart from the database using the provided user object
        // This method will vary depending on your data access mechanism (e.g., JPA, JDBC)
        return cartRepo.findByUser(user);
    }

    @Override
    public void clearCart(User user) {
        // Clear the user's cart in the database
        // This method will vary depending on your data access mechanism (e.g., JPA, JDBC)
        Cart userCart = cartRepo.findByUser(user);
        if (userCart != null) {
            userCart.getCartItems().clear(); // Clear cart items
            // Save the updated cart
            // cartRepo.save(userCart);
        }
    }


}
