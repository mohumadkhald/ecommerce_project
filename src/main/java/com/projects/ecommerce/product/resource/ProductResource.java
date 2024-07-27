package com.projects.ecommerce.product.resource;


import com.projects.ecommerce.product.dto.AllDetailsProductDto;
import com.projects.ecommerce.product.dto.ProductDto;
import com.projects.ecommerce.product.dto.ProductRequestDto;
import com.projects.ecommerce.product.dto.Spec;
import com.projects.ecommerce.product.service.ProductService;
import com.projects.ecommerce.user.service.UserService;
import com.projects.ecommerce.utilts.FileStorageService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
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
import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/products")
@Slf4j
@RequiredArgsConstructor
public class ProductResource {

	private final ProductService productService;
	private final UserService userService;
	private final FileStorageService fileStorageService;
	private final Validator validator;


	@GetMapping
	public ResponseEntity<Page<AllDetailsProductDto>> findAll(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int pageSize,
			@RequestParam(defaultValue = "createdAt") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDirection,
			@RequestParam(required = false) Double minPrice,
			@RequestParam(required = false) Double maxPrice,
			@RequestParam(required = false) String email) {

		Sort sort = Sort.by(sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
		Pageable pageable = PageRequest.of(page, pageSize, sort);

		log.info("*** ProductDto List, controller; fetch all products with filters ***");
		Page<AllDetailsProductDto> productPage = productService.findAll(pageable, minPrice, maxPrice, email);
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
		String email = userService.findById(userId).getEmail();
		productDto.setEmail(email);
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



	@PutMapping("/{productId}")
	public ResponseEntity<ProductDto> update(
			@PathVariable("productId") String productId,
			@Valid @RequestBody final ProductRequestDto productDto) {
		log.info("*** ProductDto, resource; update product with productId *");
		return ResponseEntity.ok(this.productService.update(Integer.parseInt(productId), productDto));
	}

	@GetMapping("/product-category/{subCategoryName}")
	public ResponseEntity<Page<ProductDto>> getProductsByCategoryNameAndFilters(
			@PathVariable String subCategoryName,
			@RequestParam(required = false) List<String> color,
			@RequestParam(required = false) Double minPrice,
			@RequestParam(required = false) Double maxPrice,
			@RequestParam(required = false) List<String> size, // Change parameter type to String
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int pageSize,
			@RequestParam(defaultValue = "createdAt") String sortBy,
			@RequestParam(defaultValue = "asc") String sortDirection) {

		List<String> uppercaseSizes = size != null ? size.stream().map(String::toUpperCase).collect(Collectors.toList()) : null;
		List<String> colors = color != null ? color.stream().map(String::toLowerCase).toList() : null;
		Sort sort = Sort.by(sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
		Page<ProductDto> products = productService.getProductsByCategoryNameAndFilters(subCategoryName, colors, minPrice, maxPrice, uppercaseSizes, page, pageSize, sort); // Convert size to uppercase
		return ResponseEntity.ok(products);
	}

	@GetMapping("/{subCategoryName}/{productNmae}")
	public ResponseEntity<Page<ProductDto>> getProductsByCategoryNameAndProductNameAndFilters(
			@PathVariable String subCategoryName,
			@PathVariable String productNmae,
			@RequestParam(required = false) List<String> color,
			@RequestParam(required = false) Double minPrice,
			@RequestParam(required = false) Double maxPrice,
			@RequestParam(required = false) List<String> size, // Change parameter type to String
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "5") int pageSize,
			@RequestParam(defaultValue = "createdAt") String sortBy,
			@RequestParam(defaultValue = "asc") String sortDirection) {

		List<String> uppercaseSizes = size != null ? size.stream().map(String::toUpperCase).toList() : null;
		List<String> colors = color != null ? color.stream().map(String::toLowerCase).toList() : null;
		Sort sort = Sort.by(sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
		Page<ProductDto> products = productService.getProductsByCategoryNameAndProdcutNameAndFilters(subCategoryName, productNmae, colors, minPrice, maxPrice, uppercaseSizes, page, pageSize, sort); // Convert size to uppercase
		return ResponseEntity.ok(products);
	}

	@GetMapping("/stock")
	public ResponseEntity<Map<String, Map<String, Map<String, Integer>>>> getProductVariations(
			@RequestParam String productName) {

		Map<String, Map<String, Map<String, Integer>>> variationsMap = productService.getProductVariations(productName);
		return ResponseEntity.ok(variationsMap);
	}



	@PutMapping("/{productId}/stock")
	public ResponseEntity<Map<String, String>> updateProductVariations(
			@PathVariable Integer productId,
			@RequestBody List<Spec> specs) {

		Map<String, String> response = new HashMap<>();

		if (specs == null || specs.isEmpty()) {
			response.put("message", "Specs list cannot be empty.");
			return ResponseEntity.badRequest().body(response);
		}

		Map<String, String> errors = new HashMap<>();
		for (int i = 0; i < specs.size(); i++) {
			Spec spec = specs.get(i);
			Set<ConstraintViolation<Spec>> violations = validator.validate(spec);
			for (ConstraintViolation<Spec> violation : violations) {
				errors.put("spec[" + i + "]." + violation.getPropertyPath(), violation.getMessage());
			}
		}

		if (!errors.isEmpty()) {
			return ResponseEntity.badRequest().body(errors);
		}

		productService.updateProductVariation(productId, specs);

		response.put("message", "Product Set Variations successfully");
		return ResponseEntity.ok(response);
	}


	@PostMapping("/{productId}/stock")
	public ResponseEntity<String> updateProductVariations(
			@PathVariable Integer productId,
			@RequestParam(required = false) boolean increase,
			@Valid @RequestBody List<@Valid Spec> specs) {

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


	@GetMapping("/created-by")
	public ResponseEntity<List<ProductDto>> getProductsCreatedByUser(@RequestParam("email") String email) {
		List<ProductDto> products = productService.findAllByCreatedBy(email);
		return ResponseEntity.ok(products);
	}

	@GetMapping("/find/created-by")
	public ResponseEntity<List<ProductDto>> getAllProductsCreatedByUser(@RequestHeader("Authorization") String jwtToken) {
		Integer userID = userService.findUserIdByJwt(jwtToken);
		String email = userService.findById(userID).getEmail();
		List<ProductDto> products = productService.findAllProductsByCreatedBy(email);
		return ResponseEntity.ok(products);
	}

	@DeleteMapping("/{productId}")
	public ResponseEntity<?> removeProductFromCart(@PathVariable Integer productId, @RequestHeader("Authorization") String jwtToken) {
		Integer userId = userService.findUserIdByJwt(jwtToken);
		String email = userService.findById(userId).getEmail();
		return productService.removeProductByCreatedBy(email, productId); // Pass userId and itemId to the service method
	}

	@DeleteMapping("/bulk-delete")
	public ResponseEntity<?> removeProductsFromCart(@RequestParam List<Integer> productIds, @RequestHeader("Authorization") String jwtToken) {
		Integer userId = userService.findUserIdByJwt(jwtToken);
		String email = userService.findById(userId).getEmail();
		return productService.removeProductsByCreatedBy(email, productIds);
	}

	@GetMapping("/allDetails/{productId}")
	public ResponseEntity<AllDetailsProductDto> findByProductId(
			@RequestHeader("Authorization") String jwtToken,
			@PathVariable("productId")
			@NotBlank(message = "Input must not be blank!")
			@Valid final String productId) throws AccessDeniedException {

		Integer userId = userService.findUserIdByJwt(jwtToken);
		String email = userService.findById(userId).getEmail();
		log.info("*** ProductDto, resource; fetch product by id *");
		return ResponseEntity.ok(this.productService.findByProductId(email, Integer.parseInt(productId)));
	}

	@PatchMapping("setDiscount/{productId}")
	public ResponseEntity<?> setDiscount
			(@RequestHeader("Authorization") String jwtToken,
			 @PathVariable("productId") Integer productId, @RequestParam("discount") Double discount) throws AccessDeniedException {

		Integer id = userService.findUserIdByJwt(jwtToken);
		String email = userService.findByUserId(id).getEmail();
		return  this.productService.setDiscount(email, productId, discount);
	}

	@PatchMapping("setDiscount")
	public ResponseEntity<?> setDiscounts(
			@RequestHeader("Authorization") String jwtToken,
			@RequestParam("productIds") List<Integer> productIds,
			@RequestParam("discount") Double discount) throws AccessDeniedException {

		Integer id = userService.findUserIdByJwt(jwtToken);
		String email = userService.findByUserId(id).getEmail();
		return this.productService.setDiscounts(email, productIds, discount);
	}

}










