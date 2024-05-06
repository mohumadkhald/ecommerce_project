package com.projects.ecommerce.product.service.impl;


import com.projects.ecommerce.product.domain.*;
import com.projects.ecommerce.product.dto.ProductDto;
import com.projects.ecommerce.product.dto.ProductRequestDto;
import com.projects.ecommerce.product.dto.Spec;
import com.projects.ecommerce.product.exception.wrapper.CategoryNotFoundException;
import com.projects.ecommerce.product.exception.wrapper.ProductNotFoundException;
import com.projects.ecommerce.product.helper.ProductMappingHelper;
import com.projects.ecommerce.product.repository.ProductRepository;
import com.projects.ecommerce.product.service.ProductService;
import com.projects.ecommerce.user.expetion.AlreadyExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
	
	private final ProductRepository productRepository;

	@Override
	public Page<ProductDto> findAll(Pageable pageable) {
		log.info("*** ProductDto List, service; fetch all products ***");
		return productRepository.findAll(pageable).map(ProductMappingHelper::map);
	}


	@Override
	public ProductDto findById(final Integer productId) {
		log.info("*** ProductDto, service; fetch product by id *");
		return this.productRepository.findById(productId)
				.map(ProductMappingHelper::map)
				.orElseThrow(() -> new ProductNotFoundException(String.format("Product with id: %d not found", productId)));
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
				if (existingProductByTitle != null && !existingProductByTitle.getProductId().equals(productId)) {
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
	public Page<ProductDto> getProductsByCategoryNameAndFilters(String categoryName, String color, Double minPrice, Double maxPrice, String size, int page, int pageSize, Sort sort) {
		Pageable pageable = PageRequest.of(page, pageSize, sort);

		Page<Product> productPage;
		if (color == null) {
			productPage = productRepository.findByCategoryNameAndFilters(categoryName, null, minPrice, maxPrice, size != null ? Size.valueOf(size.toUpperCase()) : null, pageable); // Convert size to uppercase
		} else {
			productPage = productRepository.findByCategoryNameAndFilters(categoryName, Color.valueOf(color), minPrice, maxPrice, size != null ? Size.valueOf(size.toUpperCase()) : null, pageable); // Convert size to uppercase
		}

		return productPage.map(ProductMappingHelper::map);
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
	public void updateProductVariation(Integer productId, Spec spec) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

		// Find the existing variation by size and color or create a new one if not found
		Optional<ProductVariation> existingVariation = product.getVariations().stream()
				.filter(variation -> variation.getSize().equals(Size.valueOf(spec.size())) && variation.getColor().equals(Color.valueOf(spec.color())))
				.findFirst();

		if (existingVariation.isPresent()) {
			// Update existing variation
			ProductVariation variationToUpdate = existingVariation.get();
			variationToUpdate.setQuantity(spec.quantity());
		} else {
			// Create new variation
			newProductVariation(product, spec, 0);
		}

		productRepository.save(product);
		int totalQuantity = product.getVariations().stream()
				.mapToInt(ProductVariation::getQuantity)
				.sum();
		product.setAllQuantity(totalQuantity);
	}


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













}









