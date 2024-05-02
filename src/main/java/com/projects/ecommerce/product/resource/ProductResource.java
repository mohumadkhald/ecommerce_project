package com.projects.ecommerce.product.resource;


import com.projects.ecommerce.product.domain.Product;
import com.projects.ecommerce.product.dto.ProductDto;
import com.projects.ecommerce.product.dto.ProductRequestDto;
import com.projects.ecommerce.product.dto.response.collection.DtoCollectionResponse;
import com.projects.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/products")
@Slf4j
@RequiredArgsConstructor
public class ProductResource {
	
	private final ProductService productService;
	
	@GetMapping
	public ResponseEntity<DtoCollectionResponse<ProductDto>> findAll() {
		log.info("*** ProductDto List, controller; fetch all categories *");
		return ResponseEntity.ok(new DtoCollectionResponse<>(this.productService.findAll()));
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
			@NotNull(message = "Input must not be NULL!")
			@Valid final ProductRequestDto productDto) {
		log.info("*** ProductDto, resource; save product ***");
		productService.save(productDto);
		Map<String, String> response = new HashMap<>();
		response.put("message", "Product created successfully");
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
	
	@DeleteMapping("/{productId}")
	public ResponseEntity<Boolean> deleteById(@PathVariable("productId") final String productId) {
		log.info("*** Boolean, resource; delete product by id *");
		this.productService.deleteById(Integer.parseInt(productId));
		return ResponseEntity.ok(true);
	}

	@GetMapping("/bycategory/{categoryName}")
	public ResponseEntity<Page<Product>> getProductsByCategoryNameAndFilters(
			@PathVariable String categoryName,
			@RequestParam(required = false) String color,
			@RequestParam(required = false) Double minPrice,
			@RequestParam(required = false) Double maxPrice,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int pageSize,
			@RequestParam(defaultValue = "desc") String sortDirection) {

		Sort sort = Sort.by("createdAt").descending();
		if (!sortDirection.equals("desc")) {
			sort = Sort.by("createdAt").ascending();
		}

		Page<Product> products = productService.getProductsByCategoryNameAndFilters(categoryName, color, minPrice, maxPrice, page, pageSize, sort);
		return ResponseEntity.ok(products);
	}
	
}










