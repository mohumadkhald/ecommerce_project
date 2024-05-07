package com.projects.ecommerce.shipping.resource;


import com.projects.ecommerce.order.domain.Cart;
import com.projects.ecommerce.order.service.CartService;
import com.projects.ecommerce.shipping.domain.OrderItem;
import com.projects.ecommerce.shipping.domain.id.OrderItemId;
import com.projects.ecommerce.shipping.dto.OrderItemDto;
import com.projects.ecommerce.shipping.dto.OrderItemsDto;
import com.projects.ecommerce.shipping.dto.response.collection.DtoCollectionResponse;
import com.projects.ecommerce.shipping.service.OrderItemService;
import com.projects.ecommerce.user.model.User;
import com.projects.ecommerce.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/shippings")
@Slf4j
@RequiredArgsConstructor
public class OrderItemResource {
	
	private final OrderItemService orderItemService;
	private final UserService userService;
	private final CartService cartService;
	
	@GetMapping
	public ResponseEntity<DtoCollectionResponse<OrderItemsDto>> findAll() {
		log.info("*** OrderItemDto List, controller; fetch all orderItems *");
		return ResponseEntity.ok(new DtoCollectionResponse<>(this.orderItemService.findAll()));
	}

	@GetMapping("my")
	public List<OrderItemsDto> findAllByCartId(@RequestHeader ("Authorization") String token
	) {

		Integer userId = userService.findUserIdByJwt(token);
		Cart cart = cartService.findCartByUserId(userId);
		log.info("*** OrderItemDto List, controller; fetch all orderItems *");
		return orderItemService.findAllByCartId(cart.getCartId());
	}
	

//	@GetMapping("/find")
//	public ResponseEntity<OrderItemsDto> findById(
//			@RequestBody
//			@NotNull(message = "Input must not be NULL")
//			@Valid final OrderItemId orderItemId) {
//		log.info("*** OrderItemDto, resource; fetch orderItem by id *");
//		return ResponseEntity.ok(this.orderItemService.findById(orderItemId));
//	}
	
	@PostMapping
	public ResponseEntity<OrderItemDto> save(
			@RequestBody 
			@NotNull(message = "Input must not be NULL")
			@Valid final OrderItemDto orderItemDto,
			@RequestHeader ("Authorization") String token
			) {

		Integer userId = userService.findUserIdByJwt(token);
		Cart cart = cartService.findCartByUserId(userId);
		orderItemDto.setOrderId(0);
		orderItemDto.setOrderNow(false);
		orderItemDto.setCartId(cart.getCartId());
		log.info("*** OrderItemDto, resource; save orderItem *");
		return ResponseEntity.ok(this.orderItemService.save(orderItemDto));
	}

	
	@PutMapping
	public ResponseEntity<OrderItemsDto> update(
			@RequestBody 
			@NotNull(message = "Input must not be NULL") 
			@Valid final OrderItemDto orderItemDto) {
		log.info("*** OrderItemDto, resource; update orderItem *");
		return ResponseEntity.ok(this.orderItemService.update(orderItemDto));
	}

	
//	@DeleteMapping("/{orderItemId}")
//	public ResponseEntity<Boolean> deleteById(
//			@PathVariable Integer orderItemId) {
//		log.info("*** Boolean, resource; delete orderItem by id *");
//		this.orderItemService.deleteById(orderItemId);
//		return ResponseEntity.ok(true);
//	}

	@DeleteMapping("/{OrderItemId}")
	public ResponseEntity<String> deleteOrderItem(@PathVariable Integer OrderItemId)
	{
		return  orderItemService.deletOrderById(OrderItemId);

	}
	
	
	
}










