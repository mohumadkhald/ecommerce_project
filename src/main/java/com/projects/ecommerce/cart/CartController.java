package com.projects.ecommerce.cart;

import com.projects.ecommerce.user.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@AllArgsConstructor
public class CartController {

    private CartService cartService;
    private UserService userService;

    @GetMapping
    public List<CartItemDto> allItem(@RequestHeader("Authorization") String jwtToken) {
        Integer userId = userService.findUserIdByJwt(jwtToken);
        return cartService.getAllItem(userId);
    }

    @PostMapping
    public CartItemDto addProductToCart(@Valid  @RequestBody CartRequest cartRequest, @RequestHeader("Authorization") String jwtToken) {
        Integer userId = userService.findUserIdByJwt(jwtToken);
        cartService.getCartByUserId(userId);
        return cartService.addProductToCart(userId, cartRequest);
    }

    @PatchMapping("/{itemId}")
    public CartItemDto editQuantity(@PathVariable Integer itemId, @RequestParam State state, @RequestHeader("Authorization") String jwtToken) {
        Integer userId = userService.findUserIdByJwt(jwtToken);
        return cartService.editQuantity(userId, itemId, String.valueOf(state));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeProductFromCart(@PathVariable Integer itemId, @RequestHeader("Authorization") String jwtToken) {
        Integer userId = userService.findUserIdByJwt(jwtToken);
        cartService.removeItemFromCart(userId, itemId); // Pass userId and itemId to the service method
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeAllItem(@RequestHeader("Authorization") String jwtToken) {
        Integer userId = userService.findUserIdByJwt(jwtToken);
        cartService.removeAllItemsFromCart(userId); // Pass userId and itemId to the service method
        return ResponseEntity.ok().build();
    }

    @GetMapping("/total")
    public ResponseEntity<Double> calculateTotal(@RequestParam Integer userId) {
        Double total = cartService.calculateTotal(userId);
        return ResponseEntity.ok(total);
    }


    @PostMapping("/sync")
    public ResponseEntity<Void> syncCart(@RequestBody List<CartRequest> cartItems, @RequestHeader("Authorization") String jwtToken) {
        Integer userId = userService.findUserIdByJwt(jwtToken);
        cartService.syncCart(userId, cartItems);
        return ResponseEntity.ok().build();
    }
}
