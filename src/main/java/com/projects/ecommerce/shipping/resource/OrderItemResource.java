package com.projects.ecommerce.shipping.resource;


import com.projects.ecommerce.order.domain.Cart;
import com.projects.ecommerce.order.service.CartService;
import com.projects.ecommerce.shipping.domain.id.OrderItemId;
import com.projects.ecommerce.shipping.dto.OrderItemDto;
import com.projects.ecommerce.shipping.dto.response.collection.DtoCollectionResponse;
import com.projects.ecommerce.shipping.service.OrderItemService;
import com.projects.ecommerce.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shippings")
@Slf4j
@RequiredArgsConstructor
public class OrderItemResource {
	
	private final OrderItemService orderItemService;
	private final UserService userService;
	private final CartService cartService;
	
	@GetMapping
	public ResponseEntity<DtoCollectionResponse<OrderItemDto>> findAll() {
		log.info("*** OrderItemDto List, controller; fetch all orderItems *");
		return ResponseEntity.ok(new DtoCollectionResponse<>(this.orderItemService.findAll()));
	}

	@GetMapping("my")
	public ResponseEntity<DtoCollectionResponse<Object>> findAllByCartId(@RequestHeader ("Authorization") String token
	) {

		Integer userId = userService.findUserIdByJwt(token);
		Cart cart = cartService.findCartByUserId(userId);
		log.info("*** OrderItemDto List, controller; fetch all orderItems *");
		return ResponseEntity.ok(new DtoCollectionResponse<>(this.orderItemService.findAllByCartId(cart.getCartId())));
	}
	
	@GetMapping("/{orderId}/{productId}")
	public ResponseEntity<OrderItemDto> findById(
			@RequestHeader ("Authorization") String token,
			@PathVariable("orderId") final Integer orderId,
			@PathVariable("productId") final Integer productId) {
		log.info("*** OrderItemDto, resource; fetch orderItem by id *");
		Integer userId = userService.findUserIdByJwt(token);
		Cart cart = cartService.findCartByUserId(userId);
		return ResponseEntity.ok(this.orderItemService.findById(
				new OrderItemId(orderId, productId)));
	}
	
	@GetMapping("/find")
	public ResponseEntity<OrderItemDto> findById(
			@RequestBody
			@NotNull(message = "Input must not be NULL")
			@Valid final OrderItemId orderItemId) {
		log.info("*** OrderItemDto, resource; fetch orderItem by id *");
		return ResponseEntity.ok(this.orderItemService.findById(orderItemId));
	}
	
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
	public ResponseEntity<OrderItemDto> update(
			@RequestBody 
			@NotNull(message = "Input must not be NULL") 
			@Valid final OrderItemDto orderItemDto) {
		log.info("*** OrderItemDto, resource; update orderItem *");
		return ResponseEntity.ok(this.orderItemService.update(orderItemDto));
	}
	
	@DeleteMapping("/{orderId}/{productId}")
	public ResponseEntity<Boolean> deleteById(
			@RequestHeader ("Authorization") String token,
			@PathVariable("orderId") final String orderId,
			@PathVariable("productId") final String productId) {
		log.info("*** Boolean, resource; delete orderItem by id *");
		this.orderItemService.deleteById(new OrderItemId(Integer.parseInt(orderId), Integer.parseInt(productId)));
		return ResponseEntity.ok(true);
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<Boolean> deleteById(
			@RequestBody 
			@NotNull(message = "Input must not be NULL") 
			@Valid final OrderItemId orderItemId) {
		log.info("*** Boolean, resource; delete orderItem by id *");
		this.orderItemService.deleteById(orderItemId);
		return ResponseEntity.ok(true);
	}
	
	
	
}










