package com.projects.ecommerce.product.service.impl;


import com.projects.ecommerce.product.domain.Color;
import com.projects.ecommerce.product.domain.Product;
import com.projects.ecommerce.product.domain.ProductVariation;
import com.projects.ecommerce.product.dto.ProductDto;
import com.projects.ecommerce.product.dto.ProductRequestDto;
import com.projects.ecommerce.product.exception.wrapper.ProductNotFoundException;
import com.projects.ecommerce.product.helper.ProductMappingHelper;
import com.projects.ecommerce.product.repository.ProductRepository;
import com.projects.ecommerce.product.service.ProductService;
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
	public ProductDto update(final Integer productId, final ProductDto productDto) {
		log.info("*** ProductDto, service; update product with productId *");
		return ProductMappingHelper.map(this.productRepository
				.save(ProductMappingHelper.map(this.findById(productId))));
	}
	
	@Override
	public void deleteById(final Integer productId) {
		log.info("*** Void, service; delete product by id *");
		this.productRepository.delete(ProductMappingHelper
				.map(this.findById(productId)));
	}


	@Override
	public Page<ProductDto> getProductsByCategoryNameAndFilters(String categoryName, String color, Double minPrice, Double maxPrice, int page, int pageSize, Sort sort) {
		Pageable pageable = PageRequest.of(page, pageSize, sort);
		Color colorObject = Color.valueOf(color); // Assuming Color is an enum with values like RED, BLUE, etc.
		Page<Product> productPage = productRepository.findByCategoryNameAndFilters(categoryName, colorObject, minPrice, maxPrice, pageable);
		return productPage.map(ProductMappingHelper::map);
	}

}









