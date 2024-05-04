package com.projects.ecommerce.product.resource;


import com.projects.ecommerce.product.domain.Size;
import com.projects.ecommerce.product.dto.ProductDto;
import com.projects.ecommerce.product.dto.ProductRequestDto;
import com.projects.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/products")
@Slf4j
@RequiredArgsConstructor
public class ProductResource {
	
	private final ProductService productService;

	@GetMapping
	public ResponseEntity<Page<ProductDto>> findAll(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int pageSize,
			@RequestParam(defaultValue = "createdAt") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDirection) {

		Sort sort = Sort.by(sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
		Pageable pageable = PageRequest.of(page, pageSize, sort);

		log.info("*** ProductDto List, controller; fetch all categories ***");
		Page<ProductDto> productPage = productService.findAll(pageable);
		return ResponseEntity.ok(productPage);
	}

	
	@GetMapping("/{productId}")
	public ResponseEntity<ProductDto> findById(
			@PathVariable("productId") 
			@NotBlank(message = "Input must not be blank!")
			@Valid final String productId) {
		log.info("*** ProductDto, resource; fetch product by id *");
		return ResponseEntity.ok(this.productService.findById(Integer.parseInt(productId)));
	}

	@PostMapping
	public ResponseEntity<Map<String, String>> save(
			@RequestBody
			@Valid final ProductRequestDto productDto) {
		log.info("*** ProductDto, resource; save product ***");
		productService.create(productDto);
		Map<String, String> response = new HashMap<>();
		response.put("message", "Product created successfully");
		return ResponseEntity.ok(response);
	}

	@PostMapping("/batch")
	public ResponseEntity<Map<String, String>> saveBatch(
			@RequestBody
			@NotNull(message = "Input must not be NULL!")
			@Valid final List<ProductRequestDto> productDtos) {
		log.info("*** ProductDto, resource; save products batch ***");
		productService.saveAll(productDtos);
		Map<String, String> response = new HashMap<>();
		response.put("message", "Products created successfully");
		return ResponseEntity.ok(response);
	}



	@PutMapping
	public ResponseEntity<ProductDto> update(
			@RequestBody 
			@NotNull(message = "Input must not be NULL!") 
			@Valid final ProductDto productDto) {
		log.info("*** ProductDto, resource; update product *");
		return ResponseEntity.ok(this.productService.update(productDto));
	}
	
	@PutMapping("/{productId}")
	public ResponseEntity<ProductDto> update(
			@PathVariable("productId")
			@NotBlank(message = "Input must not be blank!")
			@Valid final String productId,
			@RequestBody 
			@NotNull(message = "Input must not be NULL!") 
			@Valid final ProductDto productDto) {
		log.info("*** ProductDto, resource; update product with productId *");
		return ResponseEntity.ok(this.productService.update(Integer.parseInt(productId), productDto));
	}

	@GetMapping("/product-category/{categoryName}")
	public ResponseEntity<Page<ProductDto>> getProductsByCategoryNameAndFilters(
			@PathVariable String categoryName,
			@RequestParam(required = false) String color,
			@RequestParam(required = false) Double minPrice,
			@RequestParam(required = false) Double maxPrice,
			@RequestParam(required = false) String size, // Change parameter type to String
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int pageSize,
			@RequestParam(defaultValue = "desc") String sortDirection) {

		Sort sort = Sort.by("createdAt").descending();
		if (!sortDirection.equals("desc")) {
			sort = Sort.by("createdAt").ascending();
		}
		Page<ProductDto> products = productService.getProductsByCategoryNameAndFilters(categoryName, color, minPrice, maxPrice, size != null ? size.toUpperCase() : null, page, pageSize, sort); // Convert size to uppercase
		return ResponseEntity.ok(products);
	}




}










