package com.projects.ecommerce.product.helper;


import com.projects.ecommerce.product.domain.Category;
import com.projects.ecommerce.product.domain.Product;
import com.projects.ecommerce.product.domain.ProductVariation;
import com.projects.ecommerce.product.dto.CategoryDto;
import com.projects.ecommerce.product.dto.ProductDto;
import com.projects.ecommerce.product.dto.ProductRequestDto;
import com.projects.ecommerce.product.service.impl.ProductServiceImpl;

import java.util.*;

public interface ProductMappingHelper {

	public static ProductDto map(final Product product) {
		ProductDto.ProductDtoBuilder productDtoBuilder = ProductDto.builder()
				.productId(product.getProductId())
				.productTitle(product.getProductTitle())
				.imageUrl(product.getImageUrl())
				.price(product.getPrice())
				.categoryDto(
						CategoryDto.builder()
								.categoryId(product.getCategory().getCategoryId())
								.categoryTitle(product.getCategory().getCategoryTitle())
								.build());

		// Extract color and size from all variations
		Map<String, List<String>> colorsAndSizes = new HashMap<>();
		List<ProductVariation> variations = product.getVariations();
		if (variations != null && !variations.isEmpty()) {
			for (ProductVariation variation : variations) {
				String color = String.valueOf(variation.getColor());
				String size = String.valueOf(variation.getSize());

				// If color already exists, add size to its list; otherwise, create a new list for the color
				colorsAndSizes.computeIfAbsent(color, k -> new ArrayList<>()).add(size);
			}
		}

		// Set the colorsAndSizes in the builder
		productDtoBuilder.colorsAndSizes(colorsAndSizes);

		return productDtoBuilder.build();
	}


	public static Product map(final ProductDto productDto) {
		Product product = Product.builder()
				.productId(productDto.getProductId())
				.productTitle(productDto.getProductTitle())
				.imageUrl(productDto.getImageUrl())
				.price(productDto.getPrice())
				.category(
						Category.builder()
								.categoryId(productDto.getCategoryDto().getCategoryId())
								.categoryTitle(productDto.getCategoryDto().getCategoryTitle())
								.build())
				.build();

		// Create a list to hold variations
		List<ProductVariation> variations;
        variations = new ArrayList<>();

        // Create a ProductVariation object
		ProductVariation variation = ProductVariation.builder()
//				.color(productDto.getColor())
//				.size(productDto.getSize())
				.product(product)
				.build();

		variations.add(variation);

		// Set the variations list to the product
		product.setVariations(variations);

		return product;
	}


	public static Product map(final ProductRequestDto productDto, List<Product> productList) {
		// Check if a product with the same title already exists
		Optional<Product> existingProduct = productList.stream()
				.filter(p -> p.getProductTitle().equals(productDto.getProductTitle()))
				.findFirst();

		if (existingProduct.isPresent()) {
			// If the product already exists, update its details and variations
			Product product = existingProduct.get();
			int existingQuantity = product.getAllQuantity();
			product.setAllQuantity(existingQuantity + productDto.getQuantity());

			// Check if a variation with the same size and color already exists
			ProductServiceImpl.getExistingVariation(product, productDto);

			return product;
		} else {
			// If the product does not exist, create a new one
			Product product = Product.builder()
					.productId(productDto.getProductId())
					.productTitle(productDto.getProductTitle())
					.imageUrl(productDto.getImageUrl())
					.price(productDto.getPrice())
					.allQuantity(productDto.getQuantity())
					.category(
							Category.builder()
									.categoryId(productDto.getCategoryId())
									.build())
					.build();

			// Create a list to hold variations
			List<ProductVariation> variations = new ArrayList<>();

			// Create a ProductVariation object
			ProductVariation variation = ProductVariation.builder()
					.color(productDto.getColor())
					.size(productDto.getSize())
					.quantity(productDto.getQuantity())
					.product(product)
					.build();

			variations.add(variation);

			// Set the variations list to the product
			product.setVariations(variations);

			return product;
		}
	}

}













