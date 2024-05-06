package com.projects.ecommerce.order.resource;


import com.projects.ecommerce.order.dto.OrderDto;
import com.projects.ecommerce.order.dto.response.collection.DtoCollectionResponse;
import com.projects.ecommerce.order.service.OrderService;
import com.projects.ecommerce.shipping.domain.OrderItem;
import com.projects.ecommerce.shipping.service.OrderItemService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Slf4j
@RequiredArgsConstructor
public class OrderResource {
	
	private final OrderService orderService;
	private final OrderItemService orderItemService;
	
	@GetMapping
	public ResponseEntity<DtoCollectionResponse<OrderDto>> findAll() {
		log.info("*** OrderDto List, controller; fetch all orders *");
		return ResponseEntity.ok(new DtoCollectionResponse<>(this.orderService.findAll()));
	}
	
	@GetMapping("/{orderId}")
	public ResponseEntity<OrderDto> findById(
			@PathVariable("orderId")
			@NotBlank(message = "Input must not be blank")
			@Valid final String orderId) {
		log.info("*** OrderDto, resource; fetch order by id *");
		return ResponseEntity.ok(this.orderService.findById(Integer.parseInt(orderId)));
	}

	@PostMapping
	public ResponseEntity<OrderDto> save(@RequestBody @Valid final OrderDto orderDto) {
		log.info("*** OrderDto, resource; save order ***");

		List<OrderItem> orderItems = orderItemService.findAllByOrderId(orderDto.getOrderId());

		// Check if the list is not empty before accessing its elements
		if (!orderItems.isEmpty()) {
			double totalPrice = 0.0;

			for (OrderItem orderItem : orderItems) {
				totalPrice += orderItem.getTotalPrice();
			}

			orderDto.setOrderDate(LocalDateTime.now());
			orderDto.setOrderFee(totalPrice);
		} else {
			// Handle the case where no order items are found for the given order ID
			// You might want to log a warning or return an appropriate response
			log.warn("No order items found for order ID: {}", orderDto.getOrderId());
			// Return an appropriate ResponseEntity, for example:
			orderDto.setOrderFee(0.00);
		}

		return ResponseEntity.ok(this.orderService.save(orderDto));
	}

	
	@PutMapping
	public ResponseEntity<OrderDto> update(
			@RequestBody 
			@NotNull(message = "Input must not be NULL") 
			@Valid final OrderDto orderDto) {
		log.info("*** OrderDto, resource; update order *");
		return ResponseEntity.ok(this.orderService.update(orderDto));
	}
	
	@PutMapping("/{orderId}")
	public ResponseEntity<OrderDto> update(
			@PathVariable("orderId")
			@NotBlank(message = "Input must not be blank")
			@Valid final String orderId,
			@RequestBody 
			@NotNull(message = "Input must not be NULL") 
			@Valid final OrderDto orderDto) {
		log.info("*** OrderDto, resource; update order with orderId *");
		return ResponseEntity.ok(this.orderService.update(Integer.parseInt(orderId), orderDto));
	}
	
	@DeleteMapping("/{orderId}")
	public ResponseEntity<Boolean> deleteById(@PathVariable("orderId") final String orderId) {
		log.info("*** Boolean, resource; delete order by id *");
		this.orderService.deleteById(Integer.parseInt(orderId));
		return ResponseEntity.ok(true);
	}
	
	
	
}










