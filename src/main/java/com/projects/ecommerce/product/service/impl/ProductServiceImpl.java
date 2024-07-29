package com.projects.ecommerce.product.service.impl;


import com.projects.ecommerce.product.domain.*;
import com.projects.ecommerce.product.dto.AllDetailsProductDto;
import com.projects.ecommerce.product.dto.ProductDto;
import com.projects.ecommerce.product.dto.ProductRequestDto;
import com.projects.ecommerce.product.dto.Spec;
import com.projects.ecommerce.product.exception.wrapper.CategoryNotFoundException;
import com.projects.ecommerce.product.exception.wrapper.ProductNotFoundException;
import com.projects.ecommerce.product.helper.ProductMappingHelper;
import com.projects.ecommerce.product.repository.ProductRepository;
import com.projects.ecommerce.product.service.CategoryService;
import com.projects.ecommerce.product.service.ProductService;
import com.projects.ecommerce.product.service.SubCategoryService;
import com.projects.ecommerce.user.expetion.AlreadyExistsException;
import com.projects.ecommerce.user.expetion.NotFoundException;
import com.projects.ecommerce.user.expetion.UserNotFoundException;
import com.projects.ecommerce.user.model.Role;
import com.projects.ecommerce.user.repository.UserRepo;
import com.projects.ecommerce.utilts.traits.ApiTrait;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
	
	private final ProductRepository productRepository;
	private final UserRepo userRepository;
	private final SubCategoryService subCategoryService;

	@Override
	public Page<AllDetailsProductDto> findAll(Pageable pageable, Double minPrice, Double maxPrice, String email) {
		log.info("*** ProductDto List, service; fetch all products with filters ***");

		Specification<Product> spec = (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (minPrice != null) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
			}
			if (maxPrice != null) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
			}
			if (email != null && !email.isEmpty()) {
				predicates.add(criteriaBuilder.equal(root.get("createdBy"), email));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

		return productRepository.findAll(spec, pageable).map(ProductMappingHelper::map2);
	}




	@Override
	public ProductDto findById(final Integer productId) {
		log.info("*** ProductDto, service; fetch product by id *");
		return this.productRepository.findById(productId)
				.map(ProductMappingHelper::map)
				.orElseThrow(() -> new NotFoundException("Product", "Product with ID " + productId + " Not Found: "));
	}

	@Override
	public ProductDto create(final ProductRequestDto productDto) {
		log.info("*** ProductDto, service; save product ***");

		// Retrieve existing products from the repository
		List<Product> existingProducts = this.productRepository.findAll();

		// Map the incoming productDto to a Product object, considering existing products
		Product mappedProduct = ProductMappingHelper.map(productDto, existingProducts);

		// Save the mapped product to the repository and map it back to ProductDto
		return ProductMappingHelper.map(this.productRepository.save(mappedProduct));
	}

	@Override
	public List<ProductDto> saveAll(List<ProductRequestDto> productDtos) {
		log.info("*** ProductDto, service; save products ***");
		// Retrieve existing products from the repository
		List<Product> existingProducts = this.productRepository.findAll();

		Map<String, Product> productMap = new HashMap<>(); // Map to store products by name

		for (ProductRequestDto productDto : productDtos) {
			String productName = productDto.getProductTitle();
			Product existingProduct = productMap.get(productName);
			if (existingProduct != null) {
				// If the product with the same name already exists in the map, update its quantities
				updateProduct(existingProduct, productDto);
			} else {
				// If the product does not exist in the map, create a new one
				Product mappedProduct = ProductMappingHelper.map(productDto, existingProducts);
				productMap.put(productName, mappedProduct);
			}
		}

		// Save all products in the map to the repository
		List<ProductDto> savedProductDtos = new ArrayList<>();
		for (Product product : productMap.values()) {
			Product savedProduct = this.productRepository.save(product);
			savedProductDtos.add(ProductMappingHelper.map(savedProduct));
		}

		return savedProductDtos;
	}

	private void updateProduct(Product product, ProductRequestDto productDto) {
		// Implement the logic to update the existing product based on the new productDto
		// Check if a variation with the same size and color already exists
		getExistingVariation(product, productDto);
	}

	public static void getExistingVariation(Product product, ProductRequestDto productDto) {
		Optional<ProductVariation> existingVariation = product.getVariations().stream()
				.filter(v -> v.getSize().equals(productDto.getSize()) && v.getColor().equals(productDto.getColor()))
				.findFirst();

		if (existingVariation.isPresent()) {
			// If the variation already exists, update its quantity
			ProductVariation variation = existingVariation.get();
			int variationQuantity = variation.getQuantity();
			variation.setQuantity(variationQuantity + productDto.getQuantity());
		} else {
			// If the variation does not exist, create a new one
			ProductVariation newVariation = ProductVariation.builder()
					.color(productDto.getColor())
					.size(productDto.getSize())
					.quantity(productDto.getQuantity())
					.product(product)
					.build();

			product.getVariations().add(newVariation);
		}
		int totalQuantity = product.getVariations().stream()
				.mapToInt(ProductVariation::getQuantity)
				.sum();
		product.setAllQuantity(totalQuantity);
	}


	@Override
	public ProductDto update(final ProductDto productDto) {
		log.info("*** ProductDto, service; update product *");
		return ProductMappingHelper.map(this.productRepository
				.save(ProductMappingHelper.map(productDto)));
	}

	@Override
	public ProductDto update(final Integer productId, final ProductRequestDto productDto) {
		log.info("*** ProductDto, service; update product with productId *");
		try {
			// Retrieve the category by id
			Product product = productRepository.findById(productId)
					.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

			// Check if the category title is being updated
			if (!product.getProductTitle().equals(productDto.getProductTitle())) {
				// If the category title is being updated, check if the new title already exists
				Product existingProductByTitle = productRepository.findByProductTitle(productDto.getProductTitle());
				if (existingProductByTitle != null && !existingProductByTitle.getId().equals(productId)) {
					// If the new title already exists and belongs to a different category, throw an exception
					throw new AlreadyExistsException("Product", "Already Exists: " + productDto.getProductTitle());
				}
			}

//			 Map and save the updated category
			product.setProductTitle(productDto.getProductTitle());
			product.setPrice(productDto.getPrice());
			product.setImageUrl(productDto.getImageUrl());
			product.setDiscountPercent(productDto.getDiscountPercent());
			product.setSubCategory(
					SubCategory.builder()
							.subId(productDto.getSubCategoryId())
							.category(Category.builder().build())
							.build()
			);
			productRepository.save(product);
			return ProductMappingHelper.map(product);

		} catch (CategoryNotFoundException e) {
			log.error("CategoryNotFoundException: {}", e.getMessage());
			throw new CategoryNotFoundException("Category not Found: " + productDto.getProductTitle());
		} catch (AlreadyExistsException e) {
			log.error("AlreadyExistsException: {}", e.getMessage());
			throw new AlreadyExistsException("Product", "Already Exists: " + productDto.getProductTitle());
		} catch (Exception e) {
			log.error("An error occurred while updating the product with id {}: {}", productId, e.getMessage());
			throw new RuntimeException("Failed to update product", e); // Wrap and re-throw the exception
		}
	}
	
	@Override
	public void deleteById(final Integer productId) {
		log.info("*** Void, service; delete product by id *");
		this.productRepository.delete(ProductMappingHelper
				.map(this.findById(productId)));
	}


	@Override
	public Page<ProductDto> getProductsByCategoryNameAndFilters(String categoryName, List<String> colors, Double minPrice, Double maxPrice, List<String> sizes, Boolean available, int page, int pageSize, Sort sort) {
		Pageable pageable = PageRequest.of(page, pageSize, sort);
		List<Size> sizeEnums = sizes != null ? sizes.stream().map(size -> Size.valueOf(size.toUpperCase())).toList() : null;
		List<Color> colorEnums = colors != null ? colors.stream().map(color -> Color.valueOf(color.toLowerCase())).toList() : null;
		boolean subCategory = subCategoryService.findByName(categoryName);
		if (!subCategory) {
			throw new NotFoundException("Category", "Category " + categoryName + " Notfound");
		}
		Page<Product> productPage = productRepository.findByCategoryNameAndFilters(categoryName, colorEnums, minPrice, maxPrice, sizeEnums, available, pageable);

		return productPage.map(ProductMappingHelper::map);
	}

	@Override
	public Page<ProductDto> getProductsByCategoryNameAndProdcutNameAndFilters
			(String subCategoryName, String productNmae,
			 List<String> colors, Double minPrice, Double maxPrice,
			 List<String> sizes, int page, int pageSize, Sort sort) {
		Pageable pageable = PageRequest.of(page, pageSize, sort);
		List<Size> sizeEnums = sizes != null ? sizes.stream()
				.map(size -> Size.valueOf(size.toUpperCase())).toList() : null;
		List<Color> colorEnums = colors != null ? colors.stream()
				.map(color -> Color.valueOf(color.toLowerCase())).toList() : null;
		Page<Product> productPage;
		if (colors == null) {
			productPage = productRepository.findByCategoryNameAndProductTitleAndFilters
					(subCategoryName, productNmae,null, minPrice,
							maxPrice, sizeEnums, pageable); // Convert size to uppercase
		} else {
			productPage = productRepository.findByCategoryNameAndProductTitleAndFilters
					(subCategoryName, productNmae, colorEnums, minPrice,
							maxPrice, sizeEnums, pageable); // Convert size to uppercase
		}
		return productPage.map(ProductMappingHelper::map);
	}

	@Override
	public List<ProductDto> findAllByCreatedBy(String email) {
		// Retrieve products from repository based on the email of the creator
		List<Product> products = productRepository.findAllByCreatedBy(email);

		// Map the list of Product entities to a list of ProductDto using ProductMappingHelper
		return products.stream()
				.map(ProductMappingHelper::map)
				.collect(Collectors.toList());
	}

	@Override
	public List<ProductDto> findAllProductsByCreatedBy(String email) {
		// Retrieve products from repository based on the email of the creator
		List<Product> products = productRepository.findAllByCreatedBy(email);

		// Map the list of Product entities to a list of ProductDto using ProductMappingHelper
		return products.stream()
				.map(ProductMappingHelper::map)
				.collect(Collectors.toList());
	}


	@Override
	public Map<String, Map<String, Map<String, Integer>>> getProductVariations(String productName) {
		Product product = productRepository.findByProductTitle(productName);
		Map<String, Map<String, Integer>> variationsMap = new HashMap<>();

		// Iterate through product variations to populate the map
		for (ProductVariation variation : product.getVariations()) {
			String color = variation.getColor().toString();
			String size = variation.getSize().toString();
			Integer quantity = variation.getQuantity();

			variationsMap.putIfAbsent(color, new HashMap<>());
			variationsMap.get(color).put(size, quantity);
		}

		return Collections.singletonMap(productName, variationsMap);
	}


	@Override
	@Transactional
	public void updateProductVariation(Integer productId, List<Spec> specs) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

		for (Spec spec : specs) {
			Optional<ProductVariation> existingVariation = product.getVariations().stream()
					.filter(variation -> variation.getSize().equals(Size.valueOf(spec.size())) &&
							variation.getColor().equals(Color.valueOf(spec.color())))
					.findFirst();

			if (existingVariation.isPresent()) {
				// Update existing variation
				ProductVariation variationToUpdate = existingVariation.get();
				variationToUpdate.setQuantity(spec.quantity());
			} else {
				// Create new variation
				newProductVariation(product, spec, 0);
			}
		}

		productRepository.save(product);
		int totalQuantity = product.getVariations().stream()
				.mapToInt(ProductVariation::getQuantity)
				.sum();
		product.setAllQuantity(totalQuantity);
	}

//	public void updateProductVariation(Integer productId, Spec spec) {
//		Product product = productRepository.findById(productId)
//				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
//
//		// Find the existing variation by size and color or create a new one if not found
//		Optional<ProductVariation> existingVariation = product.getVariations().stream()
//				.filter(variation -> variation.getSize().equals(Size.valueOf(spec.size())) && variation.getColor().equals(Color.valueOf(spec.color())))
//				.findFirst();
//
//		if (existingVariation.isPresent()) {
//			// Update existing variation
//			ProductVariation variationToUpdate = existingVariation.get();
//			variationToUpdate.setQuantity(spec.quantity());
//		} else {
//			// Create new variation
//			newProductVariation(product, spec, 0);
//		}
//
//		productRepository.save(product);
//		int totalQuantity = product.getVariations().stream()
//				.mapToInt(ProductVariation::getQuantity)
//				.sum();
//		product.setAllQuantity(totalQuantity);
//	}


	@Override
	@Transactional
	public void updateProductStocks(Integer productId, List<Spec> specs, boolean increaseQuantity) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

		// Iterate through the provided specs
		for (Spec spec : specs) {
			// Find the existing variation by size and color or create a new one if not found
			Optional<ProductVariation> existingVariation = product.getVariations().stream()
					.filter(variation -> variation.getSize().equals(Size.valueOf(spec.size())) && variation.getColor().equals(Color.valueOf(spec.color())))
					.findFirst();

			if (existingVariation.isPresent()) {
				// Update existing variation
				ProductVariation variationToUpdate = existingVariation.get();
				int currentQuantity = variationToUpdate.getQuantity();
				variationToUpdate.setQuantity(increaseQuantity ? currentQuantity + spec.quantity() : spec.quantity());
			} else {
				// Create new variation
				newProductVariation(product, spec, spec.quantity());
			}
		}

		productRepository.save(product);
		int totalQuantity = product.getVariations().stream()
				.mapToInt(ProductVariation::getQuantity)
				.sum();
		product.setAllQuantity(totalQuantity);
	}



	private void newProductVariation(Product product, Spec spec, Integer increaseQuantity) {
		ProductVariation newVariation = new ProductVariation();
		newVariation.setSize(Size.valueOf(spec.size()));
		newVariation.setColor(Color.valueOf(spec.color()));
		newVariation.setQuantity(spec.quantity() + increaseQuantity);
		newVariation.setProduct(product);
		product.getVariations().add(newVariation);
	}

	@Override
	public Product findProductById(Integer productId) {
		return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
	}















	@Override
	@Transactional
	public void updateProductStock(Integer productId, List<Spec> specs, Integer quantityToSubtract) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

		// Iterate through the provided specs
		for (Spec spec : specs) {
			// Find the existing variation by size and color or create a new one if not found
			Optional<ProductVariation> existingVariation = product.getVariations().stream()
					.filter(variation -> variation.getSize().equals(Size.valueOf(spec.size())) && variation.getColor().equals(Color.valueOf(spec.color())))
					.findFirst();

			if (existingVariation.isPresent()) {
				// Update existing variation
				ProductVariation variationToUpdate = existingVariation.get();
				int currentQuantity = variationToUpdate.getQuantity();
				int newQuantity = currentQuantity - quantityToSubtract;
				variationToUpdate.setQuantity(Math.max(newQuantity, 0)); // Ensure not to decrease below zero
			} else {
				// Create new variation
				// Assuming the quantity to subtract will always be negative
				newProductVariation(product, spec, quantityToSubtract);
			}
		}

		productRepository.save(product);
		int totalQuantity = product.getVariations().stream()
				.mapToInt(ProductVariation::getQuantity)
				.sum();
		product.setAllQuantity(totalQuantity);
	}



	@Override
	public ResponseEntity<?> removeProductByCreatedBy(String email, Integer productId) {
		// Find the product by its ID
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

		// Check if the user is an admin
		boolean isAdmin = userRepository.findAllByRole(Role.valueOf("ADMIN"))
				.stream()
				.anyMatch(user -> user.getEmail().equals(email));

		// If the user is an admin or is the creator of the product, delete it
		if (isAdmin || product.getCreatedBy().equals(email)) {
			productRepository.deleteById(productId);
			return ApiTrait.successMessage("Product Deleted", HttpStatus.OK);
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}

	@Override
	public ResponseEntity<?> removeProductsByCreatedBy(String email, List<Integer> productIds) {
		// Find the products by their IDs
		List<Product> products = productRepository.findAllById(productIds);

		if (products.isEmpty()) {
			throw new ProductNotFoundException("Products not found with the provided ids: " + productIds);
		}

		// Check if the user is an admin
		boolean isAdmin = userRepository.findAllByRole(Role.valueOf("ADMIN"))
				.stream()
				.anyMatch(user -> user.getEmail().equals(email));

		// Filter out the products that the user is not allowed to delete
		List<Product> productsToDelete = products.stream()
				.filter(product -> isAdmin || product.getCreatedBy().equals(email))
				.collect(Collectors.toList());

		if (productsToDelete.isEmpty()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		productRepository.deleteAll(productsToDelete);
		return ApiTrait.successMessage("Products Deleted", HttpStatus.OK);
	}


	@Override
	public AllDetailsProductDto findByProductId(String email, int productId) throws AccessDeniedException {
		// Find the product by its ID
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

		boolean isAdmin = userRepository.findAllByRole(Role.ADMIN)
				.stream()
				.anyMatch(user -> user.getEmail().equals(email));

		if (isAdmin || product.getCreatedBy().equals(email)) {
			return productRepository.findById(productId)
					.map(ProductMappingHelper::map2)
					.orElseThrow(() -> new ProductNotFoundException(String.format("Product with id: %d not found", productId)));
		} else {
			throw new AccessDeniedException("You do not have permission to access this product.");
		}
	}

	@Override
	public ResponseEntity<?> setDiscount(String email, Integer productId, Double discount) throws AccessDeniedException {
		Map<String, String> response = new HashMap<>();
		// Find the product by its ID
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

		boolean isAdmin = userRepository.findAllByRole(Role.ADMIN)
				.stream()
				.anyMatch(user -> user.getEmail().equals(email));

		if (isAdmin || product.getCreatedBy().equals(email)) {
			product.setDiscountPercent(discount);
			Double discountedPrice = product.getPrice() - (product.getPrice() * discount / 100);
			product.setDiscountedPrice(discountedPrice);
			productRepository.save(product);
			response.put("message", "The Discount has been set");
			return ResponseEntity.ok(response);
		} else {
			throw new AccessDeniedException("You do not have permission to access this product.");
		}
	}

	@Override
	public ResponseEntity<?> setDiscounts(String email, List<Integer> productIds, Double discount) throws AccessDeniedException {
		Map<String, String> response = new HashMap<>();
		boolean isAdmin = userRepository.findAllByRole(Role.ADMIN)
				.stream()
				.anyMatch(user -> user.getEmail().equals(email));

		for (Integer productId : productIds) {
			Product product = productRepository.findById(productId)
					.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

			if (isAdmin || product.getCreatedBy().equals(email)) {
				product.setDiscountPercent(discount);
				Double discountedPrice = product.getPrice() - (product.getPrice() * discount / 100);
				product.setDiscountedPrice(discountedPrice);
				productRepository.save(product);
			} else {
				throw new AccessDeniedException("You do not have permission to access product with id: " + productId);
			}
		}

		response.put("message", "The Discount has been set for the specified products");
		return ResponseEntity.ok(response);
	}


}









