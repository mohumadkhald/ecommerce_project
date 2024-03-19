package com.projects.ecommerce.service;

import com.projects.ecommerce.exception.UserException;
import com.projects.ecommerce.model.Cart;
import com.projects.ecommerce.model.CartItem;
import com.projects.ecommerce.model.Product;
import com.projects.ecommerce.model.User;
import com.projects.ecommerce.repo.CartRepo;
import com.projects.ecommerce.requests.CartRequestDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

    private CartRepo cartRepo;
    private UserService userService;




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

    @Override
    public Cart createCart(CartRequestDTO cartRequestDTO, String jwtToken) throws UserException {
        // Extract user ID from JWT token
        Long userId = userService.findUserIdByJwt(jwtToken);

        // Retrieve the user by userId
        User user = userService.findUserById(userId);

        if (user == null) {
            throw new UserException("User not found");
        }

        // Create a new cart
        Cart cart = new Cart();

        // Set the user for the cart
        cart.setUser(user);

        cart.setId(userId);
        // Optionally, add cart items to the cart from cartRequestDTO

        // Save the cart
        return cartRepo.save(cart);
    }
}
