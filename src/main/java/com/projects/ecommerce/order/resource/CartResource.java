package com.projects.ecommerce.order.resource;


import com.projects.ecommerce.order.domain.Cart;
import com.projects.ecommerce.order.dto.CartDto;
import com.projects.ecommerce.order.dto.CartRequestDto;
import com.projects.ecommerce.order.dto.response.collection.DtoCollectionResponse;
import com.projects.ecommerce.order.service.CartService;
import com.projects.ecommerce.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/carts")
@Slf4j
@RequiredArgsConstructor
public class CartResource {
	
	private final CartService cartService;
	private final UserService userService;
	
	@GetMapping("all")
	public ResponseEntity<DtoCollectionResponse<CartDto>> findAll() {
		log.info("*** CartDto List, controller; fetch all categories *");
		return ResponseEntity.ok(new DtoCollectionResponse<>(this.cartService.findAll()));
	}
	
//	@GetMapping("/{cartId}")
//	public ResponseEntity<CartDto> findById(
//			@PathVariable("cartId")
//			@NotBlank(message = "Input must not be blank")
//			@Valid final String cartId) {
//		log.info("*** CartDto, resource; fetch cart by id *");
//		return ResponseEntity.ok(this.cartService.findById(Integer.parseInt(cartId)));
//	}

	@GetMapping
	public ResponseEntity<CartRequestDto> findByUserId(
			@RequestHeader ("Authorization") String token
	) {
		log.info("*** CartDto, resource; fetch cart by userId *");
		int userID = userService.findUserIdByJwt(token);
		return ResponseEntity.ok(this.cartService.findByUserId(userID));
	}
	
	@PostMapping
	public ResponseEntity<CartRequestDto> save(
			@RequestHeader ("Authorization") String token) {
		log.info("*** CartDto, resource; save cart *");
		int userID = userService.findUserIdByJwt(token);
		CartRequestDto cartDto = new CartRequestDto();
		cartDto.setUserId(userID);
		return ResponseEntity.ok(this.cartService.save(cartDto));
	}
	
	@PutMapping
	public ResponseEntity<CartDto> update(
			@RequestBody 
			@NotNull(message = "Input must not be NULL") 
			@Valid final CartDto cartDto) {
		log.info("*** CartDto, resource; update cart *");
		return ResponseEntity.ok(this.cartService.update(cartDto));
	}
	
	@PutMapping("/{cartId}")
	public ResponseEntity<CartDto> update(
			@PathVariable("cartId")
			@NotBlank(message = "Input must not be blank")
			@Valid final String cartId,
			@RequestBody 
			@NotNull(message = "Input must not be NULL") 
			@Valid final CartDto cartDto) {
		log.info("*** CartDto, resource; update cart with cartId *");
		return ResponseEntity.ok(this.cartService.update(Integer.parseInt(cartId), cartDto));
	}
	
	@DeleteMapping("/{cartId}")
	public ResponseEntity<Boolean> deleteById(@PathVariable("cartId") final String cartId) {
		log.info("*** Boolean, resource; delete cart by id *");
		this.cartService.deleteById(Integer.parseInt(cartId));
		return ResponseEntity.ok(true);
	}
	
	
	
}










