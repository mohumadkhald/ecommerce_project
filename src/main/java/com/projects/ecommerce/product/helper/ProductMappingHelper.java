package com.projects.ecommerce.product.helper;


import com.projects.ecommerce.product.domain.Category;
import com.projects.ecommerce.product.domain.Product;
import com.projects.ecommerce.product.domain.ProductVariation;
import com.projects.ecommerce.product.domain.SubCategory;
import com.projects.ecommerce.product.dto.*;
import com.projects.ecommerce.product.service.impl.ProductServiceImpl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public interface ProductMappingHelper {

	public static ProductDto map(final Product product) {
        boolean inStock = false;
        if (product.getAllQuantity() > 0) {
            inStock = true;
        }

        ProductDto.ProductDtoBuilder productDtoBuilder = ProductDto.builder()
                .productId(product.getId())
                .productTitle(product.getProductTitle())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .discountPercent(product.getDiscountPercent())
                .discountPrice(product.getDiscountedPrice())
                .inStock(inStock)
                .email(product.getCreatedBy())
                .subCategoryDto(
                        SubCategoryDto.builder()
                                .id(product.getSubCategory().getSubId())
                                .name(product.getSubCategory().getName())
                                .categoryId(product.getSubCategory().getCategory().getCategoryId())
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
				.id(productDto.getProductId())
				.createdAt(LocalDateTime.now())
				.createdBy(productDto.getEmail())
				.productTitle(productDto.getProductTitle())
				.imageUrl(productDto.getImageUrl())
				.price(productDto.getPrice())
				.discountPercent(productDto.getDiscountPercent())
				.subCategory(
						SubCategory.builder()
								.subId(productDto.getSubCategoryDto().getId())
								.name(productDto.getSubCategoryDto().getName())
								.category(Category.builder()
										.build())
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
		// Check if a same seller add product with the same title already exists
		Optional<Product> existingProduct = productList.stream()
				.filter(p -> p.getProductTitle().equals(productDto.getProductTitle()) && p.getCreatedBy().equals(productDto.getEmail()))
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
			if (productDto.getDiscountPercent() == null) {
				productDto.setDiscountPercent(0.0);
				productDto.setDiscountedPrice(0.0);
			}
			// If the product does not exist, create a new one
			Product product = Product.builder()
					.id(productDto.getProductId())
					.createdBy(productDto.getEmail())
					.createdAt(LocalDateTime.now())
					.productTitle(productDto.getProductTitle())
					.imageUrl(productDto.getImageUrl())
					.price(productDto.getPrice())
					.discountPercent(productDto.getDiscountPercent())
					.discountedPrice(productDto.getDiscountedPrice())
					.allQuantity(productDto.getQuantity())
					.subCategory(
							SubCategory.builder()
									.subId(productDto.getSubCategoryId())
									.category(Category.builder().build())
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


	static AllDetailsProductDto map2(Product product) {
		List<ProductVariationDto> productVariationDtos = product.getVariations().stream()
				.map(variation -> {
					ProductVariationDto variationDto = new ProductVariationDto();
					variationDto.setColor(String.valueOf(variation.getColor()));
					variationDto.setSize(String.valueOf(variation.getSize()));
					variationDto.setQuantity(variation.getQuantity());
					return variationDto;
				})
				.collect(Collectors.toList());

		SubCategoryDto subCategoryDto = SubCategoryDto.builder()
				.id(product.getSubCategory().getSubId())
				.name(product.getSubCategory().getName())
				.categoryId(product.getSubCategory().getCategory().getCategoryId())
				.build();

		return AllDetailsProductDto.builder()
				.productId(product.getId())
				.productTitle(product.getProductTitle())
				.imageUrl(product.getImageUrl())
				.price(product.getPrice())
				.allQuantity(product.getAllQuantity())
				.discountPercent(product.getDiscountPercent())
				.discountPrice(product.getDiscountedPrice())
				.email(product.getCreatedBy())
				.productVariations(productVariationDtos)
				.subCategoryDto(subCategoryDto)
				.build();
	}
}













