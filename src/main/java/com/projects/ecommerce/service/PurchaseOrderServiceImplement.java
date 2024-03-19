package com.projects.ecommerce.service;

import com.projects.ecommerce.exception.PurchaseOrderException;
import com.projects.ecommerce.model.*;
import com.projects.ecommerce.repo.CartRepo;
import com.projects.ecommerce.repo.PurchaseOrderRepo;
import lombok.AllArgsConstructor;
import org.assertj.core.util.Arrays;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PurchaseOrderServiceImplement implements PurchaseOrderService{
    private CartRepo cartRepo;
    private CartService cartService;
    private ProductService productService;
    private UserService userService;
    private CartItemService cartItemService;
    private PurchaseOrderRepo purchaseOrderRepo;
    @Override
    public PurchaseOrder createOrder(User user, Address shippingAddress) {

        Cart cart = cartService.getUserCart(user); // Assuming there's a method to get the user's cart
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("User's cart is empty. Cannot create an order.");
        }

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setUser(user);
        purchaseOrder.setShippingAddress(shippingAddress);
        purchaseOrder.setTotalItem(cart.getTotalItem());
        purchaseOrder.setOrderStatus(OrderStatus.PLACED);

        // Save the purchase order to the database
         purchaseOrder = purchaseOrderRepo.save(purchaseOrder); // Example if using JPA

        // Clear the user's cart
        cartService.clearCart(user);

        return purchaseOrder;
    }

    @Override
    public PurchaseOrder findPurchaseOrderById(Long orderId) throws PurchaseOrderException {
        // Example: Retrieve purchase order from database using orderId

        return purchaseOrderRepo.findById(orderId)
                 .orElseThrow(() -> new PurchaseOrderException("Purchase order not found with id: " + orderId));

    }

    @Override
    public List<PurchaseOrder> userOrdersHistory(Long userId) {
        return purchaseOrderRepo.findByUserId(userId);

//        return new ArrayList<>(); // Placeholder
    }

    @Override
    public PurchaseOrder placedOrder(Long orderId) throws PurchaseOrderException {
        PurchaseOrder order = findPurchaseOrderById(orderId);
        if (order == null) {
            throw new PurchaseOrderException("Purchase order not found with id: " + orderId);
        }

        if (!OrderStatus.PLACED.equals(order.getOrderStatus())) {
            throw new PurchaseOrderException("Cannot place order with id " + orderId + ". Order is not in PLACED status.");
        }

        order.setOrderStatus(OrderStatus.CONFIRMED); // Update status to CONFIRMED
        // Optionally, you may set other properties like orderDate, etc.

        // Persist the updated order (save to database)

        return order;
    }

    @Override
    public PurchaseOrder confirmedOrder(Long orderId) throws PurchaseOrderException {
        PurchaseOrder order = findPurchaseOrderById(orderId);
        if (order == null) {
            throw new PurchaseOrderException("Purchase order not found with id: " + orderId);
        }

        if (!OrderStatus.PLACED.equals(order.getOrderStatus())) {
            throw new PurchaseOrderException("Cannot confirm order with id " + orderId + ". Order is not in PLACED status.");
        }

        order.setOrderStatus(OrderStatus.CONFIRMED); // Update status to CONFIRMED
        // Optionally, you may set other properties like orderDate, etc.

        // Persist the updated order (save to database)

        return order;
    }

    @Override
    public PurchaseOrder shippedOrder(Long orderId) throws PurchaseOrderException {
        PurchaseOrder order = findPurchaseOrderById(orderId);
        if (order == null) {
            throw new PurchaseOrderException("Purchase order not found with id: " + orderId);
        }

        if (!OrderStatus.CONFIRMED.equals(order.getOrderStatus())) {
            throw new PurchaseOrderException("Cannot ship order with id " + orderId + ". Order is not in CONFIRMED status.");
        }

        order.setOrderStatus(OrderStatus.SHIPPED); // Update status to SHIPPED
        // Optionally, you may set other properties like deliveryDate, etc.

        // Persist the updated order (save to database)

        return order;
    }

    @Override
    public PurchaseOrder deliveredOrder(Long orderId) throws PurchaseOrderException {
        // Fetch the purchase order from the database
        PurchaseOrder purchaseOrder = purchaseOrderRepo.findById(orderId)
                .orElseThrow(() -> new PurchaseOrderException("Purchase order not found with id: " + orderId));

        // Check if the order is in a state that allows it to be marked as delivered
        if (!purchaseOrder.getOrderStatus().equals(OrderStatus.SHIPPED)) {
            throw new PurchaseOrderException("Cannot mark order as delivered. Order status is not 'SHIPPED'.");
        }

        // Update the order status to 'DELIVERED'
        purchaseOrder.setOrderStatus(OrderStatus.DELIVERED);

        // Update the delivery date
        purchaseOrder.setDeliveryDate(LocalDateTime.now());

        // Save the updated purchase order
        // purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

        return purchaseOrder;
    }

    @Override
    public PurchaseOrder canceledOrder(Long orderId) throws PurchaseOrderException {
        // Fetch the purchase order from the database
        PurchaseOrder purchaseOrder = purchaseOrderRepo.findById(orderId)
                .orElseThrow(() -> new PurchaseOrderException("Purchase order not found with id: " + orderId));

        // Check if the order is in a state that allows it to be canceled
        if (!Arrays.asList(OrderStatus.PLACED).contains(purchaseOrder.getOrderStatus())) {
            throw new PurchaseOrderException("Cannot cancel order. Order status is not 'PLACED' or 'CONFIRMED'.");
        }

        // Update the order status to 'CANCELED'
        purchaseOrder.setOrderStatus(OrderStatus.CANCELED);

        // Save the updated purchase order
        // purchaseOrder = purchaseOrderRepository.save(purchaseOrder);

        return purchaseOrder;
    }

    @Override
    public List<PurchaseOrder> getAllPurchaseOrders() {
        // Retrieve all purchase orders from the database
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepo.findAll();
        return purchaseOrders;
    }

    @Override
    public void deletePurchaseOrder(Long orderId) throws PurchaseOrderException {
        // Fetch the purchase order from the database
        PurchaseOrder purchaseOrder = purchaseOrderRepo.findById(orderId)
                .orElseThrow(() -> new PurchaseOrderException("Purchase order not found with id: " + orderId));

        // Delete the purchase order
        // purchaseOrderRepository.delete(purchaseOrder);
    }

}
