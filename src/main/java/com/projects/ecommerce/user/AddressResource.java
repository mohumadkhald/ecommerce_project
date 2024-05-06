package com.projects.ecommerce.user;


import com.projects.ecommerce.user.dto.AddressDto;
import com.projects.ecommerce.user.dto.AddressRequestDto;
import com.projects.ecommerce.user.dto.DtoCollectionResponse;
import com.projects.ecommerce.user.model.Address;
import com.projects.ecommerce.user.repository.AddressRepository;
import com.projects.ecommerce.user.service.AddressService;
import com.projects.ecommerce.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping(value = {"/api/address"})
@Slf4j
@RequiredArgsConstructor
public class AddressResource {
	
	private final AddressService addressService;
	private final AddressRepository addressRepository;
	private final UserService userService;
	
	@GetMapping
	public ResponseEntity<DtoCollectionResponse<AddressDto>> findAll() {
		log.info("*** AddressDto List, controller; fetch all addresss *");
		return ResponseEntity.ok(new DtoCollectionResponse<>(this.addressService.findAll()));
	}
	
	@GetMapping("/{addressId}")
	public ResponseEntity<AddressDto> findById(
			@PathVariable("addressId")
			@NotBlank(message = "Input must not blank")
			@Valid final String addressId) {
		log.info("*** AddressDto, resource; fetch address by id *");
		return ResponseEntity.ok(this.addressService.findById(Integer.parseInt(addressId.strip())));
	}

	@GetMapping("/{userId}/addresses")
	public ResponseEntity<DtoCollectionResponse<AddressRequestDto>> getUserAddresses(@PathVariable Integer userId) {
			// Call the service method to get addresses for the user
		return ResponseEntity.ok(new DtoCollectionResponse<>(this.addressService.getUserAddresses(userId)));

	}

	@PostMapping
	public ResponseEntity<AddressRequestDto> save(
			@RequestHeader("Authorization") String jwtToken,
			@RequestBody 
			@NotNull(message = "Input must not NULL")
			@Valid final AddressRequestDto addressRequestDto) {
		Integer userId = userService.findUserIdByJwt(jwtToken);
		addressRequestDto.setUserId(userId);
		log.info("*** AddressDto, resource; save address *");
		return ResponseEntity.ok(this.addressService.save(addressRequestDto));
	}
	
	@PutMapping
	public ResponseEntity<AddressDto> update(
			@RequestHeader("Authorization") String jwtToken,
			@RequestBody 
			@NotNull(message = "Input must not NULL") 
			@Valid final AddressRequestDto addressRequestDto) {
		Integer userId = userService.findUserIdByJwt(jwtToken);
		addressRequestDto.setUserId(userId);
		log.info("*** AddressDto, resource; update address *");
		return ResponseEntity.ok(this.addressService.update(addressRequestDto));
	}
	
	@PutMapping("/{addressId}")
	public ResponseEntity<AddressDto> update(
			@PathVariable("addressId") 
			@NotBlank(message = "Input must not blank") final String addressId, 
			@RequestBody 
			@NotNull(message = "Input must not NULL") 
			@Valid final AddressDto addressDto) {
		log.info("*** AddressDto, resource; update address with addressId *");
		return ResponseEntity.ok(this.addressService.update(Integer.parseInt(addressId.strip()), addressDto));
	}
	
	@DeleteMapping("/{addressId}")
	public ResponseEntity<Boolean> deleteById(@PathVariable("addressId") @NotBlank(message = "Input must not blank") @Valid final String addressId) {
		log.info("*** Boolean, resource; delete address by id *");
		this.addressService.deleteById(Integer.parseInt(addressId));
		return ResponseEntity.ok(true);
	}
	
	
	
}










