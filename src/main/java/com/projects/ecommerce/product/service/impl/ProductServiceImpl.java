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
import com.projects.ecommerce.product.service.ProductService;
import com.projects.ecommerce.product.service.SubCategoryService;
import com.projects.ecommerce.user.expetion.AlreadyExistsException;
import com.projects.ecommerce.user.expetion.NotFoundException;
import com.projects.ecommerce.user.model.Role;
import com.projects.ecommerce.user.repository.UserRepo;
import com.projects.ecommerce.utilts.traits.ApiTrait;
import jakarta.persistence.criteria.*;
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

//	public Page<AllDetailsProductDto> findAll(Pageable pageable, Double minPrice, Double maxPrice, List<String> color, List<String> size, Boolean available, String email, String productTitle) {
//		log.info("*** ProductDto List, service; fetch all products with filters ***");
//
//		Specification<Product> spec = (root, query, criteriaBuilder) -> {
//			List<Predicate> predicates = new ArrayList<>();
//
//			if (minPrice != null) {
//				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
//			}
//			if (maxPrice != null) {
//				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
//			}
//			if (email != null && !email.isEmpty()) {
//				predicates.add(criteriaBuilder.equal(root.get("createdBy"), email));
//			}
//			if (productTitle != null && !productTitle.isEmpty()) {
//				predicates.add(criteriaBuilder.like(root.get("productTitle"), "%" + productTitle + "%"));
//			}
//
//			if (color != null && !color.isEmpty()) {
//				Join<Product, ProductVariation> variationJoin = root.join("variations");
//				List<Color> colorEnums = color.stream()
//						.map(Color::valueOf)
//						.collect(Collectors.toList());
//				predicates.add(variationJoin.get("color").in(colorEnums));
//			}
//			if (size != null && !size.isEmpty()) {
//				Join<Product, ProductVariation> variationJoin = root.join("variations");
//				List<Size> sizeEnums = size.stream()
//						.map(Size::valueOf)
//						.collect(Collectors.toList());
//				predicates.add(variationJoin.get("size").in(sizeEnums));
//			}
//			if (available != null) {
//				if (available) {
//					predicates.add(criteriaBuilder.greaterThan(root.get("allQuantity"), 0));
//				} else {
//					predicates.add(criteriaBuilder.equal(root.get("allQuantity"), 0));
//				}
//			}
//
//			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
//		};
//
//		return productRepository.findAll(spec, pageable).map(ProductMappingHelper::map2);
//	}


	@Override
	public Page<ProductDto> getProductsByCategoryNameAndFilters(String subCategoryName, List<String> colors, Double minPrice, Double maxPrice, List<String> sizes, Boolean available, int page, int pageSize, Sort sort) {
		return getFilteredProducts(subCategoryName, null, colors, minPrice, maxPrice, sizes, available, page, pageSize, sort);
	}

	@Override
	public Page<ProductDto> getProductsByCategoryNameAndProdcutNameAndFilters(String subCategoryName, String productName, List<String> colors, Double minPrice, Double maxPrice, List<String> sizes, int page, int pageSize, Sort sort) {
		return getFilteredProducts(subCategoryName, productName, colors, minPrice, maxPrice, sizes, null, page, pageSize, sort);
	}
	@Override
	public Page<AllDetailsProductDto> findAll(Pageable pageable, Double minPrice, Double maxPrice, List<String> colors, List<String> sizes, Boolean available, String email, String productTitle) {
		log.info("*** ProductDto List, service; fetch all products with filters ***");

		Specification<Product> spec = (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			// Basic predicates
			if (minPrice != null) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
			}
			if (maxPrice != null) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
			}
			if (email != null && !email.isEmpty()) {
				predicates.add(criteriaBuilder.equal(root.get("createdBy"), email));
			}
			if (productTitle != null && !productTitle.isEmpty()) {
				predicates.add(criteriaBuilder.like(root.get("productTitle"), "%" + productTitle + "%"));
			}

			// Join with ProductVariation
			Join<Product, ProductVariation> variationJoin = root.join("variations");

			// filter size and color must exist
			fiterSizeAndColor(colors, sizes, root, query, criteriaBuilder, predicates);


			// Ensure distinct products
			query.distinct(true);

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

		return productRepository.findAll(spec, pageable).map(ProductMappingHelper::map2);
	}


	private Page<ProductDto> getFilteredProducts(String categoryName, String productName, List<String> colors, Double minPrice, Double maxPrice, List<String> sizes, Boolean available, int page, int pageSize, Sort sort) {
		Pageable pageable = PageRequest.of(page, pageSize, sort);

		// Check if the category exists
		if (!subCategoryService.findByName(categoryName)) {
			throw new NotFoundException("Category", "Category " + categoryName + " Not found");
		}

		Specification<Product> spec = (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			// Category predicate
			predicates.add(criteriaBuilder.equal(root.get("subCategory").get("name"), categoryName));

			// Product name predicate
			if (productName != null && !productName.isEmpty()) {
				predicates.add(criteriaBuilder.like(root.get("productTitle"), "%" + productName + "%"));
			}

			// Price predicates
			if (minPrice != null) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
			}
			if (maxPrice != null) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
			}

			// Availability predicate
			if (available != null) {
				if (available) {
					predicates.add(criteriaBuilder.greaterThan(root.get("allQuantity"), 0));
				} else {
					predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("allQuantity"), 0));
				}
			}

			// Size and color filters
			fiterSizeAndColor(colors, sizes, root, query, criteriaBuilder, predicates);

			query.distinct(true);
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

		return productRepository.findAll(spec, pageable).map(ProductMappingHelper::map);
	}

	private static void fiterSizeAndColor(List<String> colors, List<String> sizes, Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
		// Color Filter
		if (colors != null && !colors.isEmpty()) {
			List<Color> colorEnums = colors.stream()
					.map(Color::valueOf)
					.toList();

			// Ensure product has all specified colors
			Subquery<Long> colorSubquery = query.subquery(Long.class);
			Root<ProductVariation> colorRoot = colorSubquery.from(ProductVariation.class);
			colorSubquery.select(colorRoot.get("product").get("id"))
					.where(
							criteriaBuilder.and(
									colorRoot.get("color").in(colorEnums),
									criteriaBuilder.equal(colorRoot.get("product").get("id"), root.get("id")),
									criteriaBuilder.greaterThan(colorRoot.get("quantity"), 0)  // Ensure quantity > 0
							)
					)
					.groupBy(colorRoot.get("product").get("id"))
					.having(
							criteriaBuilder.equal(
									criteriaBuilder.countDistinct(colorRoot.get("color")),
									(long) colorEnums.size()
							)
					);

			Predicate hasAllColors = criteriaBuilder.exists(colorSubquery);
			predicates.add(hasAllColors);
		}

		// Size Filter
		if (sizes != null && !sizes.isEmpty()) {
			List<Size> sizeEnums = sizes.stream()
					.map(Size::valueOf)
					.toList();

			// Ensure product has all specified sizes
			Subquery<Long> sizeSubquery = query.subquery(Long.class);
			Root<ProductVariation> sizeRoot = sizeSubquery.from(ProductVariation.class);
			sizeSubquery.select(sizeRoot.get("product").get("id"))
					.where(
							criteriaBuilder.and(
									sizeRoot.get("size").in(sizeEnums),
									criteriaBuilder.equal(sizeRoot.get("product").get("id"), root.get("id")),
									criteriaBuilder.greaterThan(sizeRoot.get("quantity"), 0)  // Ensure quantity > 0
							)
					)
					.groupBy(sizeRoot.get("product").get("id"))
					.having(
							criteriaBuilder.equal(
									criteriaBuilder.countDistinct(sizeRoot.get("size")),
									(long) sizeEnums.size()
							)
					);

			Predicate hasAllSizes = criteriaBuilder.exists(sizeSubquery);
			predicates.add(hasAllSizes);
		}

		// Color and Size Combination
		if ((colors != null && !colors.isEmpty()) && (sizes != null && !sizes.isEmpty())) {
			Subquery<Long> combinedSubquery = query.subquery(Long.class);
			Root<ProductVariation> combinedRoot = combinedSubquery.from(ProductVariation.class);

			List<Predicate> colorSizePredicates = new ArrayList<>();
			if (!colors.isEmpty()) {
				List<Color> colorEnums = colors.stream()
						.map(Color::valueOf)
						.toList();
				colorSizePredicates.add(combinedRoot.get("color").in(colorEnums));
			}
			if (!sizes.isEmpty()) {
				List<Size> sizeEnums = sizes.stream()
						.map(Size::valueOf)
						.toList();
				colorSizePredicates.add(combinedRoot.get("size").in(sizeEnums));
			}

			combinedSubquery.select(combinedRoot.get("product").get("id"))
					.where(
							criteriaBuilder.and(
									criteriaBuilder.equal(combinedRoot.get("product").get("id"), root.get("id")),
									criteriaBuilder.and(colorSizePredicates.toArray(new Predicate[0])),
									criteriaBuilder.greaterThan(combinedRoot.get("quantity"), 0)  // Ensure quantity > 0
							)
					)
					.groupBy(combinedRoot.get("product").get("id"))
					.having(
							criteriaBuilder.equal(
									criteriaBuilder.countDistinct(combinedRoot.get("id")),
									((long) colors.size() * sizes.size())
							)
					);

			Predicate hasAllColorSizeCombinations = criteriaBuilder.exists(combinedSubquery);
			predicates.add(hasAllColorSizeCombinations);
		}
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
	public List<ProductDto> saveAll(List<ProductRequestDto> productDtos, String email) {
		log.info("*** ProductDto, service; save products ***");
		// Retrieve existing products from the repository
		List<Product> existingProducts = this.productRepository.findAll();

		// Map to store products by name
		Map<String, Product> productMap = new HashMap<>();
		for (Product product : existingProducts) {
			productMap.put(product.getProductTitle(), product);
		}

		for (ProductRequestDto productDto : productDtos) {
			String productName = productDto.getProductTitle();
			Product existingProduct = productMap.get(productName);
			log.info(String.valueOf(existingProduct));

			if (existingProduct != null && existingProduct.getSubCategory().getSubId().equals(productDto.getSubCategoryId())) {
				// If the product with the same name and email already exists in the map, update its quantities
				if (existingProduct.getCreatedBy() != null && existingProduct.getCreatedBy().equals(email)) {
					updateProduct(existingProduct, productDto);
				}
			} else {
				// If the product does not exist in the map, or the email does not match, create a new one
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
	public void updateProductVariation(Integer productId, List<Spec> specs, List<String> imageUrls) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

		for (int i = 0; i < specs.size(); i++) {
			Spec spec = specs.get(i);
			Optional<ProductVariation> existingVariation = product.getVariations().stream()
					.filter(variation -> variation.getSize().equals(Size.valueOf(spec.getSize())) &&
							variation.getColor().equals(Color.valueOf(spec.getColor())))
					.findFirst();

			if (existingVariation.isPresent()) {
				// Update existing variation
				ProductVariation variationToUpdate = existingVariation.get();
				variationToUpdate.setQuantity(spec.getQuantity());
				if (i < imageUrls.size()) {
					variationToUpdate.setImg(imageUrls.get(i));
				}
			} else {
				log.info(String.valueOf(imageUrls));
				// Create new variation
				String imageUrl = (i < imageUrls.size()) ? imageUrls.get(i) : null;
				newProductVariation(product, spec, 0, imageUrl);
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
					.filter(variation -> variation.getSize().equals(Size.valueOf(spec.getSize())) && variation.getColor().equals(Color.valueOf(spec.getColor())))
					.findFirst();

			if (existingVariation.isPresent()) {
				// Update existing variation
				ProductVariation variationToUpdate = existingVariation.get();
				int currentQuantity = variationToUpdate.getQuantity();
				variationToUpdate.setQuantity(increaseQuantity ? currentQuantity + spec.getQuantity() : spec.getQuantity());
			} else {
				// Create new variation
				newProductVariation(product, spec, spec.getQuantity(), null);
			}
		}

		productRepository.save(product);
		int totalQuantity = product.getVariations().stream()
				.mapToInt(ProductVariation::getQuantity)
				.sum();
		product.setAllQuantity(totalQuantity);
	}

	private void newProductVariation(Product product, Spec spec, Integer increaseQuantity, String img) {
		ProductVariation newVariation = new ProductVariation();
		newVariation.setSize(Size.valueOf(spec.getSize()));
		newVariation.setColor(Color.valueOf(spec.getColor()));
		newVariation.setQuantity(spec.getQuantity() + increaseQuantity);
		newVariation.setImg(img);
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
					.filter(variation -> variation.getSize().equals(Size.valueOf(spec.getSize())) && variation.getColor().equals(Color.valueOf(spec.getColor())))
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
				newProductVariation(product, spec, quantityToSubtract, null);
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
				.orElseThrow(() -> new NotFoundException("product", "Product not found with id: " + productId));

		boolean isAdmin = userRepository.findAllByRole(Role.ADMIN)
				.stream()
				.anyMatch(user -> user.getEmail().equals(email));

		if (isAdmin || product.getCreatedBy().equals(email)) {
			return productRepository.findById(productId)
					.map(ProductMappingHelper::map2)
					.orElseThrow(() -> new NotFoundException("product", "Product not found with id: " + productId));
		} else {
			throw new AccessDeniedException("You do not have permission to access this product.");
		}
	}

	@Override
	public ResponseEntity<?> setDiscount(String email, Integer productId, Double discount) throws AccessDeniedException {
		Map<String, String> response = new HashMap<>();
		// Find the product by its ID
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new NotFoundException("product", "Product not found with id: " + productId));

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









