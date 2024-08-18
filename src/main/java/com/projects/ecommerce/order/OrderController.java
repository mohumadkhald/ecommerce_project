package com.projects.ecommerce.order;

import com.projects.ecommerce.user.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {

    private OrderService orderService;
    private UserService userService;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestHeader("Authorization") String jwtToken,
                                             @Valid @RequestBody OrderRequest orderRequest,
                                                @RequestParam(required = false) boolean removeNotFoundStock
    ) {
        Integer userId = userService.findUserIdByJwt(jwtToken);
        OrderDto order = orderService.createOrder(userId, orderRequest.getPaymentInfo(), orderRequest.getAddress(), removeNotFoundStock);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Integer orderId, 
                                                  @RequestBody OrderStatusUpdateRequest statusUpdateRequest) {
        orderService.updateOrderStatus(orderId, statusUpdateRequest.getStatus());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Integer orderId) {
        OrderDto order = orderService.findById(orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("user")
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(@RequestHeader("Authorization") String jwtToken) {
        Integer userId = userService.findUserIdByJwt(jwtToken);
        List<OrderDto> orders = orderService.findByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping
    public ResponseEntity<List<OrderDtoAdmin>> getAllOrders(@RequestHeader("Authorization") String jwtToken) {
        Integer userId = userService.findUserIdByJwt(jwtToken);
        List<OrderDtoAdmin> orders = orderService.findAll();
        return ResponseEntity.ok(orders);
    }
}
