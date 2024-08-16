package com.projects.ecommerce.order;

import com.projects.ecommerce.cart.Cart;
import com.projects.ecommerce.cart.CartItem;
import com.projects.ecommerce.cart.CartService;
import com.projects.ecommerce.product.domain.ProductVariation;
import com.projects.ecommerce.user.expetion.NotFoundException;
import com.projects.ecommerce.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private OrderRepository orderRepository;
    private CartService cartService;
    private UserService userService;

    @Transactional
    public OrderDto createOrder(Integer userId, PaymentInfo paymentInfo,
                                Address address, boolean removeNotFoundStock) {
        // Retrieve the user's cart
        Cart cart = cartService.getCartByUserId(userId);

        // Collect errors and valid cart items
        Map<String, Map<String, Object>> errors = new HashMap<>();
        List<CartItem> validCartItems = new ArrayList<>();

        // Check if each ProductVariation in the cart has enough quantity
        for (CartItem cartItem : cart.getItems()) {
            ProductVariation productVariation = cartItem.getProductVariation();
            int availableQuantity = productVariation.getQuantity();
            int orderQuantity = cartItem.getQuantity();
            if (orderQuantity > availableQuantity) {
                String itemTitle = cartItem.getProductVariation().getProduct().getProductTitle();
                Map<String, Object> errorDetails = new HashMap<>();
                errorDetails.put("message", "Quantity is greater than available");
                errorDetails.put("availableQuantity", availableQuantity);
                errorDetails.put("requestedQuantity", orderQuantity);
                errors.put(itemTitle, errorDetails);

                if (removeNotFoundStock) {
                    // If removing out-of-stock items, skip them
                    continue;
                }
            }
            validCartItems.add(cartItem);
        }

        if (!errors.isEmpty() && !removeNotFoundStock) {
            throw new StockNotFoundException(errors, "Stock issues detected");
        }

        // Calculate the total price based on valid items only
        double totalPrice = validCartItems.stream()
                .mapToDouble(CartItem::getPrice)
                .sum();

        if (totalPrice == 0) {
            throw new NotFoundException("Cart", "Cart is empty or all items are out of stock");
        }

        // Initialize the order
        Order order = Order.builder()
                .user(userService.findByUserId(userId))
                .totalPrice(totalPrice)
                .status(OrderStatus.PENDING)
                .paymentInfo(paymentInfo)
                .shippingAddress(address)
                .orderDate(LocalDateTime.now())
                .deliveryDate(LocalDateTime.now().plusDays(3))
                .build();

        // Save the order first to generate the ID
        order = orderRepository.save(order);

        // Convert valid CartItems to OrderItems and associate them with the order
        Order finalOrder = order;
        List<OrderItem> orderItems = validCartItems.stream()
                .map(cartItem -> OrderItem.builder()
                        .productVariation(cartItem.getProductVariation())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getPrice())
                        .order(finalOrder) // Associate the saved order with each OrderItem
                        .build())
                .collect(Collectors.toList());

        // Set the orderItems for the order
        order.setOrderItems(orderItems);

        // Save the order again with the orderItems
        order = orderRepository.save(order);

        if (removeNotFoundStock) {
            // Remove only the items included in the order from the cart
            validCartItems.forEach(cartItem -> cartService.removeItemFromCart(userId, cartItem.getId()));
        } else {
            // Remove all items from the cart (if not removing out-of-stock items)
            cartService.removeAllItemsFromCart(userId);
        }

        // Return the mapped OrderDto
        return OrderMappingHelper.map(order);
    }

    public void updateOrderStatus(Integer orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    public OrderDto findById(Integer orderId) {
        return OrderMappingHelper.map(orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found")));
    }

    public List<OrderDto> findByUserId(Integer userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(OrderMappingHelper::map)
                .collect(Collectors.toList());
    }

}
