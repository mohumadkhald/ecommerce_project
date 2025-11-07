package com.projects.ecommerce.product.service.impl;


import com.projects.ecommerce.product.domain.*;
import com.projects.ecommerce.product.dto.*;
import com.projects.ecommerce.product.exception.wrapper.CategoryNotFoundException;
import com.projects.ecommerce.product.exception.wrapper.ProductNotFoundException;
import com.projects.ecommerce.product.helper.ProductMappingHelper;
import com.projects.ecommerce.product.repository.ProductRepository;
import com.projects.ecommerce.product.service.CategoryService;
import com.projects.ecommerce.product.service.SubCategoryService;
import com.projects.ecommerce.user.expetion.AlreadyExistsException;
import com.projects.ecommerce.user.expetion.NotFoundException;
import com.projects.ecommerce.user.model.Role;
import com.projects.ecommerce.user.repository.UserRepo;
import com.projects.ecommerce.utilts.traits.ApiTrait;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.io.File;

import java.nio.file.Path;
import java.util.Comparator;

import static com.projects.ecommerce.product.service.impl.ProductOwnerServiceImpl.getResponseEntity;
//
//@Service
//@Transactional
//@Slf4j
//@RequiredArgsConstructor
//public class ProductServiceImpl implements ProductService {
//
//	private final ProductRepository productRepository;
//	private final UserRepo userRepository;
//	private final CategoryService categoryService;
//	private final SubCategoryService subCategoryService;
//
//
//	@Override
//	public Page<ProductDto> getProductsByCategoryNameAndFilters(String subCategoryName, String email, List<String> colors, Double minPrice, Double maxPrice, List<String> sizes, Boolean available, int page, int pageSize, Sort sort) {
//		return getFilteredProducts(subCategoryName, email, null, colors, minPrice, maxPrice, sizes, available, page, pageSize, sort);
//	}
//
//	@Override
//	public Page<ProductDto> getProductsByCategoryNameAndProdcutNameAndFilters(String category, String productName, List<String> colors, Double minPrice, Double maxPrice, List<String> sizes, Boolean available, int page, int pageSize, Sort sort) {
//		return getFilteredProductsByName(category, null, productName, colors, minPrice, maxPrice, sizes, available, page, pageSize, sort);
//	}
//
//	private Page<ProductDto> getFilteredProductsByName(String categoryName, String email, String productName, List<String> colors, Double minPrice, Double maxPrice, List<String> sizes, Boolean available, int page, int pageSize, Sort sort) {
//		Pageable pageable = PageRequest.of(page, pageSize, sort);
//		Integer categoryId;
//
//		// Check if we are filtering by a specific category or all categories
//		if (!"all".equalsIgnoreCase(categoryName)) {
//			Category opCat = categoryService.findByCategoryName(categoryName);
//			if (opCat == null) {
//                categoryId = null;
//                throw new NotFoundException("Category", "Category " + categoryName + " Not found");
//			} else {
//				categoryId = opCat.getCategoryId();
//			}
//		} else {
//            categoryId = null;
//        }
//
//        Specification<Product> spec = (root, query, criteriaBuilder) -> {
//			List<Predicate> predicates = new ArrayList<>();
//
//			// Filter by categoryId through subCategory -> category -> categoryId if categoryId is specified
//			if (categoryId != null) {
//				predicates.add(criteriaBuilder.equal(root.get("subCategory").get("category").get("categoryId"), categoryId));
//			}
//
//			if (email != null && !email.isEmpty()) {
//				predicates.add(criteriaBuilder.equal(root.get("createdBy"), email));
//			}
//
//			// Product name predicate
//			if (productName != null && !productName.isEmpty()) {
//				predicates.add(criteriaBuilder.like(root.get("productTitle"), "%" + productName + "%"));
//			}
//
//			// Price predicates
//			if (minPrice != null) {
//				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
//			}
//			if (maxPrice != null) {
//				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
//			}
//
//			// Availability predicate
//			if (available != null) {
//				if (available) {
//					predicates.add(criteriaBuilder.greaterThan(root.get("allQuantity"), 0));
//				} else {
//					predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("allQuantity"), 0));
//				}
//			}
//
//			// Size and color filters
//			filterSizeAndColor(colors, sizes, root, query, criteriaBuilder, predicates);
//
//			query.distinct(true);
//			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
//		};
//
//		return productRepository.findAll(spec, pageable).map(ProductMappingHelper::map);
//	}
//
//	@Override
//	public Page<AllDetailsProductDto> findAll(Pageable pageable, Double minPrice, Double maxPrice, List<String> colors, List<String> sizes, Boolean available, String email, String subCat,String productTitle) {
//		return getFilteredProductsAll(subCat, email, productTitle, colors, minPrice, maxPrice, sizes, available, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
//
//	}
//
//	private Page<AllDetailsProductDto> getFilteredProductsAll(String categoryName, String email, String productName, List<String> colors, Double minPrice, Double maxPrice, List<String> sizes, Boolean available, int page, int pageSize, Sort sort) {
//		Pageable pageable = PageRequest.of(page, pageSize, sort);
//
//		Specification<Product> spec = (root, query, criteriaBuilder) -> {
//			List<Predicate> predicates = new ArrayList<>();
//
//			// Category predicate
//			if (categoryName != null && !categoryName.isEmpty()) {
//				predicates.add(criteriaBuilder.equal(root.get("subCategory").get("name"), categoryName));
//			}
//			if (email != null && !email.isEmpty()) {
//				predicates.add(criteriaBuilder.equal(root.get("createdBy"), email));
//			}
//
//			// Product name predicate
//			if (productName != null && !productName.isEmpty()) {
//				predicates.add(criteriaBuilder.like(root.get("productTitle"), "%" + productName + "%"));
//			}
//
//			// Price predicates
//			if (minPrice != null) {
//				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
//			}
//			if (maxPrice != null) {
//				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
//			}
//
//			// Availability predicate
//			if (available != null) {
//				if (available) {
//					predicates.add(criteriaBuilder.greaterThan(root.get("allQuantity"), 0));
//				} else {
//					predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("allQuantity"), 0));
//				}
//			}
//
//			// Size and color filters
//			filterSizeAndColor(colors, sizes, root, query, criteriaBuilder, predicates);
//
//			query.distinct(true);
//			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
//		};
//
//		return productRepository.findAll(spec, pageable).map(ProductMappingHelper::map2);
//	}
//
//
//	private Page<ProductDto> getFilteredProducts(String categoryName, String email, String productName, List<String> colors, Double minPrice, Double maxPrice, List<String> sizes, Boolean available, int page, int pageSize, Sort sort) {
//		Pageable pageable = PageRequest.of(page, pageSize, sort);
//
//		// Check if the category exists
//		boolean opSub = subCategoryService.findByName(categoryName);
//		if (!opSub) {
//			throw new NotFoundException("Category", "Category " + categoryName + " Not found");
//		}
//
//		Specification<Product> spec = (root, query, criteriaBuilder) -> {
//			List<Predicate> predicates = new ArrayList<>();
//
//			if (categoryName != null && !categoryName.isEmpty()) {
//				predicates.add(criteriaBuilder.like(root.get("subCategory").get("name"), "%" + categoryName + "%"));
////				predicates.add(criteriaBuilder.like(root.get("category").get("categoryTitle"), "%" + categoryName + "%"));
//			}
//
//			if (email != null && !email.isEmpty()) {
//				predicates.add(criteriaBuilder.equal(root.get("createdBy"), email));
//			}
//
//			// Product name predicate
//			if (productName != null && !productName.isEmpty()) {
//				predicates.add(criteriaBuilder.like(root.get("productTitle"), "%" + productName + "%"));
//			}
//
//			// Price predicates
//			if (minPrice != null) {
//				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
//			}
//			if (maxPrice != null) {
//				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
//			}
//
//			// Availability predicate
//			if (available != null) {
//				if (available) {
//					predicates.add(criteriaBuilder.greaterThan(root.get("allQuantity"), 0));
//				} else {
//					predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("allQuantity"), 0));
//				}
//			}
//
//			// Size and color filters
//			filterSizeAndColor(colors, sizes, root, query, criteriaBuilder, predicates);
//
//			query.distinct(true);
//			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
//		};
//
//		return productRepository.findAll(spec, pageable).map(ProductMappingHelper::map);
//	}
//
//	static void filterSizeAndColor(List<String> colors, List<String> sizes, Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
//		// Color Filter
//		if (colors != null && !colors.isEmpty()) {
//			List<Color> colorEnums = colors.stream()
//					.map(Color::valueOf)
//					.toList();
//
//			// Ensure product has all specified colors
//			Subquery<Long> colorSubquery = query.subquery(Long.class);
//			Root<ProductVariation> colorRoot = colorSubquery.from(ProductVariation.class);
//			colorSubquery.select(colorRoot.get("product").get("id"))
//					.where(
//							criteriaBuilder.and(
//									colorRoot.get("color").in(colorEnums),
//									criteriaBuilder.equal(colorRoot.get("product").get("id"), root.get("id")),
//									criteriaBuilder.greaterThan(colorRoot.get("quantity"), 0)  // Ensure quantity > 0
//							)
//					)
//					.groupBy(colorRoot.get("product").get("id"))
//					.having(
//							criteriaBuilder.equal(
//									criteriaBuilder.countDistinct(colorRoot.get("color")),
//									(long) colorEnums.size()
//							)
//					);
//
//			Predicate hasAllColors = criteriaBuilder.exists(colorSubquery);
//			predicates.add(hasAllColors);
//		}
//
//		// Size Filter
//		if (sizes != null && !sizes.isEmpty()) {
//			List<Size> sizeEnums = sizes.stream()
//					.map(Size::valueOf)
//					.toList();
//
//			// Ensure product has all specified sizes
//			Subquery<Long> sizeSubquery = query.subquery(Long.class);
//			Root<ProductVariation> sizeRoot = sizeSubquery.from(ProductVariation.class);
//			sizeSubquery.select(sizeRoot.get("product").get("id"))
//					.where(
//							criteriaBuilder.and(
//									sizeRoot.get("size").in(sizeEnums),
//									criteriaBuilder.equal(sizeRoot.get("product").get("id"), root.get("id")),
//									criteriaBuilder.greaterThan(sizeRoot.get("quantity"), 0)  // Ensure quantity > 0
//							)
//					)
//					.groupBy(sizeRoot.get("product").get("id"))
//					.having(
//							criteriaBuilder.equal(
//									criteriaBuilder.countDistinct(sizeRoot.get("size")),
//									(long) sizeEnums.size()
//							)
//					);
//
//			Predicate hasAllSizes = criteriaBuilder.exists(sizeSubquery);
//			predicates.add(hasAllSizes);
//		}
//
//		// Color and Size Combination
//		if ((colors != null && !colors.isEmpty()) && (sizes != null && !sizes.isEmpty())) {
//			Subquery<Long> combinedSubquery = query.subquery(Long.class);
//			Root<ProductVariation> combinedRoot = combinedSubquery.from(ProductVariation.class);
//
//			List<Predicate> colorSizePredicates = new ArrayList<>();
//			if (!colors.isEmpty()) {
//				List<Color> colorEnums = colors.stream()
//						.map(Color::valueOf)
//						.toList();
//				colorSizePredicates.add(combinedRoot.get("color").in(colorEnums));
//			}
//			if (!sizes.isEmpty()) {
//				List<Size> sizeEnums = sizes.stream()
//						.map(Size::valueOf)
//						.toList();
//				colorSizePredicates.add(combinedRoot.get("size").in(sizeEnums));
//			}
//
//			combinedSubquery.select(combinedRoot.get("product").get("id"))
//					.where(
//							criteriaBuilder.and(
//									criteriaBuilder.equal(combinedRoot.get("product").get("id"), root.get("id")),
//									criteriaBuilder.and(colorSizePredicates.toArray(new Predicate[0])),
//									criteriaBuilder.greaterThan(combinedRoot.get("quantity"), 0)  // Ensure quantity > 0
//							)
//					)
//					.groupBy(combinedRoot.get("product").get("id"))
//					.having(
//							criteriaBuilder.equal(
//									criteriaBuilder.countDistinct(combinedRoot.get("id")),
//									((long) colors.size() * sizes.size())
//							)
//					);
//
//			Predicate hasAllColorSizeCombinations = criteriaBuilder.exists(combinedSubquery);
//			predicates.add(hasAllColorSizeCombinations);
//		}
//	}
//
//	@Override
//	public ProductDto findById(final Integer productId) {
//		log.info("*** ProductDto, service; fetch product by id *");
//		return this.productRepository.findById(productId)
//				.map(ProductMappingHelper::map)
//				.orElseThrow(() -> new NotFoundException("Product", "Product with ID " + productId + " Not Found: "));
//	}
//
//	@Override
//	public ProductDto create(final ProductRequestDto productDto) {
//		log.info("*** ProductDto, service; save product ***");
//		if (productDto.getDiscountPercent() != null)
//		{
//			productDto.setDiscountedPrice();
//		}
//		// Retrieve existing products from the repository
//		List<Product> existingProducts = this.productRepository.findAll();
//
//		// Map the incoming productDto to a Product object, considering existing products
//		Product mappedProduct = ProductMappingHelper.map(productDto, existingProducts);
//
//		// Save the mapped product to the repository and map it back to ProductDto
//		return ProductMappingHelper.map(this.productRepository.save(mappedProduct));
//	}
//
//	@Override
//	public List<ProductDto> saveAll(List<ProductRequestDto> productDtos, String email) {
//		log.info("*** ProductDto, service; save products ***");
//		// Retrieve existing products from the repository
//		List<Product> existingProducts = this.productRepository.findAll();
//
//		// Map to store products by name
//		Map<String, Product> productMap = new HashMap<>();
//		for (Product product : existingProducts) {
//			productMap.put(product.getProductTitle(), product);
//		}
//
//		for (ProductRequestDto productDto : productDtos) {
//			if (productDto.getDiscountPercent() != null)
//			{
//				productDto.setDiscountedPrice();
//			}
//			String productName = productDto.getProductTitle();
//			Product existingProduct = productMap.get(productName);
//			log.info(String.valueOf(existingProduct));
//
//			if (existingProduct != null && existingProduct.getSubCategory().getSubId().equals(productDto.getSubCategoryId())) {
//				// If the product with the same name and email already exists in the map, update its quantities
//				if (existingProduct.getCreatedBy() != null && existingProduct.getCreatedBy().equals(email)) {
//					updateProduct(existingProduct, productDto);
//				}
//			} else {
//				// If the product does not exist in the map, or the email does not match, create a new one
//				Product mappedProduct = ProductMappingHelper.map(productDto, existingProducts);
//				productMap.put(productName, mappedProduct);
//			}
//		}
//
//		// Save all products in the map to the repository
//		List<ProductDto> savedProductDtos = new ArrayList<>();
//		for (Product product : productMap.values()) {
//			ProductMappingHelper.map(this.productRepository.save(product));
//		}
//
//		return savedProductDtos;
//	}
//
//	private void updateProduct(Product product, ProductRequestDto productDto) {
//		// Implement the logic to update the existing product based on the new productDto
//		// Check if a variation with the same size and color already exists
//		getExistingVariation(product, productDto);
//	}
//
//
//	public static void getExistingVariation(Product product, ProductRequestDto productDto) {
//		Optional<ProductVariation> existingVariation = product.getVariations().stream()
//				.filter(v -> v.getSize().equals(productDto.getSize()) && v.getColor().equals(productDto.getColor()))
//				.findFirst();
//
//		if (existingVariation.isPresent()) {
//			// If the variation already exists, update its quantity
//			ProductVariation variation = existingVariation.get();
//			int variationQuantity = variation.getQuantity();
//			variation.setQuantity(variationQuantity + productDto.getQuantity());
//		} else {
//			// If the variation does not exist, create a new one
//			ProductVariation newVariation = ProductVariation.builder()
//					.color(productDto.getColor())
//					.size(productDto.getSize())
//					.quantity(productDto.getQuantity())
//					.product(product)
//					.build();
//
//			product.getVariations().add(newVariation);
//		}
//		int totalQuantity = product.getVariations().stream()
//				.mapToInt(ProductVariation::getQuantity)
//				.sum();
//		product.setAllQuantity(totalQuantity);
//	}
//
//	@Override
//	public ProductDto update(final ProductDto productDto) {
//		log.info("*** ProductDto, service; update product *");
//		return ProductMappingHelper.map(this.productRepository
//				.save(ProductMappingHelper.map(productDto)));
//	}
//
//	@Override
//	public ProductDto update(final Integer productId, final @Valid ProductEditDto productDto) {
//		log.info("*** ProductDto, service; update product with productId *");
//		try {
//			// Retrieve the category by id
//			Product product = productRepository.findById(productId)
//					.orElseThrow(() -> new NotFoundException("Product", "not found with id: " + productId));
//
//			// Check if the category title is being updated
//			if (!product.getProductTitle().equals(productDto.getProductTitle())) {
//				// If the category title is being updated, check if the new title already exists
//				Product existingProductByTitle = productRepository.findByProductTitle(productDto.getProductTitle());
//				if (existingProductByTitle != null && !existingProductByTitle.getId().equals(productId)) {
//					// If the new title already exists and belongs to a different category, throw an exception
//					throw new AlreadyExistsException("Product", "Already Exists: " + productDto.getProductTitle());
//				}
//			}
//
////			 Map and save the updated category
//			product.setProductTitle(productDto.getProductTitle());
//			product.setPrice(productDto.getPrice());
//			product.setImageUrl(productDto.getImageUrl());
//			product.setDiscountPercent(productDto.getDiscountPercent());
//			product.setSubCategory(
//					SubCategory.builder()
//							.subId(productDto.getSubCategoryId())
//							.category(Category.builder().build())
//							.build()
//			);
//			productRepository.save(product);
//			return ProductMappingHelper.map(product);
//
//		} catch (CategoryNotFoundException e) {
//			log.error("CategoryNotFoundException: {}", e.getMessage());
//			throw new CategoryNotFoundException("Category not Found: " + productDto.getProductTitle());
//		} catch (AlreadyExistsException e) {
//			log.error("AlreadyExistsException: {}", e.getMessage());
//			throw new AlreadyExistsException("Product", "Already Exists: " + productDto.getProductTitle());
//		} catch (Exception e) {
//			log.error("An error occurred while updating the product with id {}: {}", productId, e.getMessage());
//			throw new RuntimeException("Failed to update product", e); // Wrap and re-throw the exception
//		}
//	}
//
//	@Override
//	public void deleteById(final Integer productId) {
//		log.info("*** Void, service; delete product by id *");
//		this.productRepository.delete(ProductMappingHelper
//				.map(this.findById(productId)));
//	}
//
//
//	@Override
//	public List<ProductDto> findAllByCreatedBy(String email) {
//		// Retrieve products from repository based on the email of the creator
//		List<Product> products = productRepository.findAllByCreatedBy(email);
//
//		// Map the list of Product entities to a list of ProductDto using ProductMappingHelper
//		return products.stream()
//				.map(ProductMappingHelper::map)
//				.collect(Collectors.toList());
//	}
//
//	@Override
//	public List<ProductDto> findAllProductsByCreatedBy(String email) {
//		// Retrieve products from repository based on the email of the creator
//		List<Product> products = productRepository.findAllByCreatedBy(email);
//
//		// Map the list of Product entities to a list of ProductDto using ProductMappingHelper
//		return products.stream()
//				.map(ProductMappingHelper::map)
//				.collect(Collectors.toList());
//	}
//
//
//	@Override
//	public Map<String, Map<String, Map<String, Integer>>> getProductVariations(String productName) {
//		Product product = productRepository.findByProductTitle(productName);
//		Map<String, Map<String, Integer>> variationsMap = new HashMap<>();
//
//		// Iterate through product variations to populate the map
//		for (ProductVariation variation : product.getVariations()) {
//			String color = variation.getColor().toString();
//			String size = variation.getSize().toString();
//			Integer quantity = variation.getQuantity();
//
//			variationsMap.putIfAbsent(color, new HashMap<>());
//			variationsMap.get(color).put(size, quantity);
//		}
//
//		return Collections.singletonMap(productName, variationsMap);
//	}
//
//
//	@Override
//	@Transactional
//	public void updateProductVariation(Integer productId, List<Spec> specs, List<String> imageUrls) {
//		Product product = productRepository.findById(productId)
//				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
//
//		for (int i = 0; i < specs.size(); i++) {
//			Spec spec = specs.get(i);
//			Optional<ProductVariation> existingVariation = product.getVariations().stream()
//					.filter(variation -> variation.getSize().equals(Size.valueOf(spec.getSize())) &&
//							variation.getColor().equals(Color.valueOf(spec.getColor())))
//					.findFirst();
//
//			if (existingVariation.isPresent()) {
//				// Update existing variation
//				ProductVariation variationToUpdate = existingVariation.get();
//				variationToUpdate.setQuantity(spec.getQuantity());
//				if (i < imageUrls.size()) {
//					variationToUpdate.setImg(imageUrls.get(i));
//				}
//			} else {
//				log.info(String.valueOf(imageUrls));
//				// Create new variation
//				String imageUrl = (i < imageUrls.size()) ? imageUrls.get(i) : null;
//				newProductVariation(product, spec, 0, imageUrl);
//			}
//		}
//
//		productRepository.save(product);
//		int totalQuantity = product.getVariations().stream()
//				.mapToInt(ProductVariation::getQuantity)
//				.sum();
//		product.setAllQuantity(totalQuantity);
//	}
//
//	@Override
//	@Transactional
//	public void updateProductStocks(Integer productId, List<Spec> specs, boolean increaseQuantity) {
//		Product product = productRepository.findById(productId)
//				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
//
//		// Iterate through the provided specs
//		for (Spec spec : specs) {
//			// Find the existing variation by size and color or create a new one if not found
//			Optional<ProductVariation> existingVariation = product.getVariations().stream()
//					.filter(variation -> variation.getSize().equals(Size.valueOf(spec.getSize())) && variation.getColor().equals(Color.valueOf(spec.getColor())))
//					.findFirst();
//
//			if (existingVariation.isPresent()) {
//				// Update existing variation
//				ProductVariation variationToUpdate = existingVariation.get();
//				int currentQuantity = variationToUpdate.getQuantity();
//				variationToUpdate.setQuantity(increaseQuantity ? currentQuantity + spec.getQuantity() : spec.getQuantity());
//			} else {
//				// Create new variation
//				newProductVariation(product, spec, spec.getQuantity(), null);
//			}
//		}
//
//		productRepository.save(product);
//		int totalQuantity = product.getVariations().stream()
//				.mapToInt(ProductVariation::getQuantity)
//				.sum();
//		product.setAllQuantity(totalQuantity);
//	}
//
//	private void newProductVariation(Product product, Spec spec, Integer increaseQuantity, String img) {
//		ProductVariation newVariation = new ProductVariation();
//		newVariation.setSize(Size.valueOf(spec.getSize()));
//		newVariation.setColor(Color.valueOf(spec.getColor()));
//		newVariation.setQuantity(spec.getQuantity() + increaseQuantity);
//		newVariation.setImg(img);
//		newVariation.setProduct(product);
//		product.getVariations().add(newVariation);
//	}
//
//	@Override
//	public Product findProductById(Integer productId) {
//		return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
//	}
//
//	@Override
//	@Transactional
//	public void updateProductStock(Integer productId, List<Spec> specs, Integer quantityToSubtract) {
//		Product product = productRepository.findById(productId)
//				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
//
//		// Iterate through the provided specs
//		for (Spec spec : specs) {
//			// Find the existing variation by size and color or create a new one if not found
//			Optional<ProductVariation> existingVariation = product.getVariations().stream()
//					.filter(variation -> variation.getSize().equals(Size.valueOf(spec.getSize())) && variation.getColor().equals(Color.valueOf(spec.getColor())))
//					.findFirst();
//
//			if (existingVariation.isPresent()) {
//				// Update existing variation
//				ProductVariation variationToUpdate = existingVariation.get();
//				int currentQuantity = variationToUpdate.getQuantity();
//				int newQuantity = currentQuantity - quantityToSubtract;
//				variationToUpdate.setQuantity(Math.max(newQuantity, 0)); // Ensure not to decrease below zero
//			} else {
//				// Create new variation
//				// Assuming the quantity to subtract will always be negative
//				newProductVariation(product, spec, quantityToSubtract, null);
//			}
//		}
//
//		productRepository.save(product);
//		int totalQuantity = product.getVariations().stream()
//				.mapToInt(ProductVariation::getQuantity)
//				.sum();
//		product.setAllQuantity(totalQuantity);
//	}
//
//	@Override
//	public ResponseEntity<?> removeProductByCreatedBy(String email, Integer productId) {
//		// Find the product by its ID
//		Product product = productRepository.findById(productId)
//				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
//
//		// Check if the user is an admin
//		boolean isAdmin = userRepository.findAllByRole(Role.valueOf("ADMIN"))
//				.stream()
//				.anyMatch(user -> user.getEmail().equals(email));
//
//		// If the user is an admin or is the creator of the product, delete it
//		return getResponseEntity(email, productId, product, isAdmin, productRepository);
//	}
//
//	@Override
//	public ResponseEntity<?> removeProductsByCreatedBy(String email, List<Integer> productIds) {
//		// Find the products by their IDs
//		List<Product> products = productRepository.findAllById(productIds);
//
//		if (products.isEmpty()) {
//			throw new ProductNotFoundException("Products not found with the provided ids: " + productIds);
//		}
//
//		// Check if the user is an admin
//		boolean isAdmin = userRepository.findAllByRole(Role.valueOf("ADMIN"))
//				.stream()
//				.anyMatch(user -> user.getEmail().equals(email));
//
//		// Filter out the products that the user is not allowed to delete
//		List<Product> productsToDelete = products.stream()
//				.filter(product -> isAdmin || product.getCreatedBy().equals(email))
//				.collect(Collectors.toList());
//
//		if (productsToDelete.isEmpty()) {
//			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//		}
//
//		// Delete the product folders and their contents
//		return getResponseEntity(productsToDelete, productRepository);
//	}
//
//	@Override
//	public AllDetailsProductDto findByProductId(String email, int productId) throws AccessDeniedException {
//		// Find the product by its ID
//		return getAllDetailsProductDto(email, productId, productRepository, userRepository);
//	}
//
//	@Override
//	public ResponseEntity<?> setDiscount(String email, Integer productId, Double discount) throws AccessDeniedException {
//		Map<String, String> response = new HashMap<>();
//		// Find the product by its ID
//		Product product = productRepository.findById(productId)
//				.orElseThrow(() -> new NotFoundException("product", "Product not found with id: " + productId));
//
//		boolean isAdmin = userRepository.findAllByRole(Role.ADMIN)
//				.stream()
//				.anyMatch(user -> user.getEmail().equals(email));
//
//		if (isAdmin || product.getCreatedBy().equals(email)) {
//			product.setDiscountPercent(discount);
//			Double discountedPrice = product.getPrice() - (product.getPrice() * discount / 100);
//			product.setDiscountedPrice(discountedPrice);
//			productRepository.save(product);
//			response.put("message", "The Discount has been set");
//			return ResponseEntity.ok(response);
//		} else {
//			throw new AccessDeniedException("You do not have permission to access this product.");
//		}
//	}
//
//	@Override
//	public ResponseEntity<?> setDiscounts(String email, List<Integer> productIds, Double discount) throws AccessDeniedException {
//		Map<String, String> response = new HashMap<>();
//		boolean isAdmin = userRepository.findAllByRole(Role.ADMIN)
//				.stream()
//				.anyMatch(user -> user.getEmail().equals(email));
//
//		for (Integer productId : productIds) {
//			Product product = productRepository.findById(productId)
//					.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
//
//			if (isAdmin || product.getCreatedBy().equals(email)) {
//				product.setDiscountPercent(discount);
//				Double discountedPrice = product.getPrice() - (product.getPrice() * discount / 100);
//				product.setDiscountedPrice(discountedPrice);
//				productRepository.save(product);
//			} else {
//				throw new AccessDeniedException("You do not have permission to access product with id: " + productId);
//			}
//		}
//
//		response.put("message", "The Discount has been set for the specified products");
//		return ResponseEntity.ok(response);
//	}
//
//	@Override
//	public List<String> getAllEmailSellers(Integer subId) {
//		List<Product> products = null;
//		if (subId != 0)
//		{
//			products = productRepository.findBySubcategoryId(subId); // Fetch by subcategory ID
//		} else {
//			products = productRepository.findAll(); // Fetch by subcategory ID
//		}
//		return products.stream()
//				.map(Product::getCreatedBy)
//				.distinct() // Optional: to avoid duplicates
//				.collect(Collectors.toList());
//	}
//
//	@Override
//	public List<ProductDto> getSuggestionProductsBySubCategory(Integer subId) {
//		List<Product> products = productRepository.findBySubcategoryId(subId);
//
//		if (products.isEmpty()) {
//			return Collections.emptyList();
//		}
//
//		// Shuffle the list to get randomness
//		Collections.shuffle(products);
//
//		// Limit to 5 random products
//		return products.stream()
//				.limit(10)
//				.map(ProductMappingHelper::map) // convert to DTO
//				.collect(Collectors.toList());
//	}
//
//}
