package com.projects.ecommerce.product.resource;


import com.projects.ecommerce.product.dto.ProductDto;
import com.projects.ecommerce.product.dto.ProductRequestDto;
import com.projects.ecommerce.product.dto.Spec;
import com.projects.ecommerce.product.service.ProductService;
import com.projects.ecommerce.user.service.UserService;
import com.projects.ecommerce.utilts.FileStorageService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;


@RestController
@RequestMapping("/api/products")
@Slf4j
@RequiredArgsConstructor
public class ProductResource {

	private final ProductService productService;
	private final UserService userService;
	private final FileStorageService fileStorageService;

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
			@ModelAttribute
			@Valid final ProductRequestDto productDto,
            @RequestPart(value = "image", required = false) MultipartFile image,
			@RequestHeader("Authorization") String jwtToken
	) throws IOException {
		Integer userId = userService.findUserIdByJwt(jwtToken);
		log.info("*** ProductDto, resource; save product ***");
		if (image != null)
		{
			String imageUrl = fileStorageService.storeFile(image, "products" + "/"+ productDto.getProductTitle());
			productDto.setImageUrl(imageUrl);
		}
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
			@Valid final ProductRequestDto productDto) {
		log.info("*** ProductDto, resource; update product with productId *");
		return ResponseEntity.ok(this.productService.update(Integer.parseInt(productId), productDto));
	}

	@GetMapping("/product-category/{subCategoryName}")
	public ResponseEntity<Page<ProductDto>> getProductsByCategoryNameAndFilters(
			@PathVariable String subCategoryName,
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
		Page<ProductDto> products = productService.getProductsByCategoryNameAndFilters(subCategoryName, color, minPrice, maxPrice, size != null ? size.toUpperCase() : null, page, pageSize, sort); // Convert size to uppercase
		return ResponseEntity.ok(products);
	}

	@GetMapping("/stock")
	public ResponseEntity<Map<String, Map<String, Map<String, Integer>>>> getProductVariations(
			@RequestParam String productName) {

		Map<String, Map<String, Map<String, Integer>>> variationsMap = productService.getProductVariations(productName);
		return ResponseEntity.ok(variationsMap);
	}





	@PutMapping("/{productId}/stock")
	public ResponseEntity<String> updateProductVariation(
			@PathVariable Integer productId,
			@RequestBody Spec spec) {

		productService.updateProductVariation(productId, spec);

		return ResponseEntity.ok("Product variations updated successfully.");
	}

	@PostMapping("/{productId}/stock")
	public ResponseEntity<String> updateProductVariations(
			@PathVariable Integer productId,
			@RequestParam(required = false) boolean increase,
			@RequestBody List<Spec> specs) {


		productService.updateProductStocks(productId, specs, increase);

		return ResponseEntity.ok("Product variations updated successfully.");
	}

	@PostMapping("/{productId}/stocks")
	public ResponseEntity<String> updateProductStock(
			@PathVariable Integer productId,
			@RequestParam(required = false) boolean decrease,
			@RequestBody List<Spec> specs) {

		productService.updateProductStocks(productId, specs, decrease);

		String message = decrease ? "Product variations decreased successfully." : "Product variations increased successfully.";
		return ResponseEntity.ok(message);
	}

//	@PostMapping("/{productId}/stock/decrease")
//	public ResponseEntity<String> updateProductVariationsDecrease(
//			@PathVariable Integer productId,
//			@RequestParam(required = false) boolean decrease,
//			@RequestBody List<Spec> specs) {
//
//
//		productService.updateProductStocksDecrease(productId, specs, decrease);
//
//		return ResponseEntity.ok("Product variations updated successfully.");
//	}
}










