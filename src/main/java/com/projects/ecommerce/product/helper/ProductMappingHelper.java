package com.projects.ecommerce.product.helper;


import com.projects.ecommerce.product.domain.*;
import com.projects.ecommerce.product.dto.*;
import com.projects.ecommerce.product.service.impl.ProductVariationServiceImpl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public interface ProductMappingHelper {

	public static ProductDto map(final Product product) {
		// Determine if the product is in stock based on its total quantity
		boolean inStock = product.getAllQuantity() > 0;
		// Map the product variations to ProductVariationDto
		List<ProductVariationDto> productVariationDtos = product.getVariations().stream()
				.map(variation -> ProductVariationDto.builder()
						.color(String.valueOf(variation.getColor()))
						.size(String.valueOf(variation.getSize()))
						.quantity(variation.getQuantity())
						.build())
				.collect(Collectors.toList());

		// Initialize the builder for ProductDto
		ProductDto.ProductDtoBuilder productDtoBuilder = ProductDto.builder()
				.productId(product.getId())
				.productTitle(product.getProductTitle())
				.imageUrls(
						product.getImages()
								.stream()
								.map(ProductImage::getUrl)   // <-- extract only URLs
								.toList()
				)
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
                                .categoryName(product.getSubCategory().getCategory().getCategoryTitle())
								.build())
				.productVariations(productVariationDtos);  // Set the product variations

		// Map to hold colors and their corresponding sizes
//		Map<String, List<String>> colorsAndSizes = new HashMap<>();
//		List<ProductVariation> variations = product.getVariations();
//
//		if (variations != null && !variations.isEmpty()) {
//			// Iterate through the variations to populate colorsAndSizes
//			for (ProductVariation variation : variations) {
//				String color = String.valueOf(variation.getColor());
//				String size = String.valueOf(variation.getSize());
//
//				// If the color already exists, add the size to its list; otherwise, create a new list
//				colorsAndSizes.computeIfAbsent(color, k -> new ArrayList<>()).add(size);
//			}
//		}

		// Set the colorsAndSizes map in the builder
//		productDtoBuilder.colorsAndSizes(colorsAndSizes);

		// Build and return the ProductDto
		return productDtoBuilder.build();
	}


	public static Product map(final ProductDto productDto) {
		Product product = Product.builder()
				.id(productDto.getProductId())
				.createdBy(productDto.getEmail())
				.createdAt(LocalDateTime.now())
				.productTitle(productDto.getProductTitle())
				.price(productDto.getPrice())
				.discountPercent(productDto.getDiscountPercent())
				.subCategory(
						SubCategory.builder()
								.subId(productDto.getSubCategoryDto().getId())
								.category(Category.builder().build())
								.build())
				.build();

		List<ProductImage> images = productDto.getImageUrls().stream()
				.map(url -> ProductImage.builder()
						.url(url)
						.product(product)   // link image to product
						.build())
				.collect(Collectors.toList());

		product.setImages(images);

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

		Optional<Product> existingProduct = productList.stream()
				.filter(p -> p.getProductTitle().equals(productDto.getProductTitle()) &&
						p.getCreatedBy().equals(productDto.getEmail()))
				.findFirst();

		if (existingProduct.isPresent()) {
			Product product = existingProduct.get();
			product.setAllQuantity(product.getAllQuantity() + productDto.getQuantity());

			ProductVariationServiceImpl.getExistingVariation(product, productDto);

			return product;
		}

		// Discount handling
		if (productDto.getDiscountPercent() == null) {
			productDto.setDiscountPercent(0.0);
			productDto.setDiscountedPrice(0.0);
		}

// Convert List<String> → List<ProductImage>
		Product product = Product.builder()
				.id(productDto.getProductId())
				.createdBy(productDto.getEmail())
				.createdAt(LocalDateTime.now())
				.productTitle(productDto.getProductTitle())
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

		List<ProductImage> images = productDto.getImageUrls().stream()
				.map(url -> ProductImage.builder()
						.url(url)
						.product(product)
						.build())
				.collect(Collectors.toList());
		product.setImages(images);
		// Variations
		ProductVariation variation = ProductVariation.builder()
				.color(productDto.getColor())
				.size(productDto.getSize())
				.quantity(productDto.getQuantity())
				.product(product)
				.build();

		product.setVariations(List.of(variation));

		return product;
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
                .categoryName(product.getSubCategory().getCategory().getCategoryTitle())
                .img(product.getSubCategory().getImg())
				.build();

		return AllDetailsProductDto.builder()
				.productId(product.getId())
				.productTitle(product.getProductTitle())
				.imageUrls(
						product.getImages().stream()
								.map(ProductImage::getUrl)
								.collect(Collectors.toList())
				)

				.price(product.getPrice())
				.allQuantity(product.getAllQuantity())
				.discountPercent(product.getDiscountPercent())
				.discountPrice(product.getDiscountedPrice())
				.email(product.getCreatedBy())
				.productVariations(productVariationDtos)
				.subCategoryDto(subCategoryDto)
                .createdOn(product.getCreatedAt())
                .updatedOn(product.getUpdatedAt())
				.build();
	}

	static ProductVariation map(final ProductVariationDto productVariationDto) {
		if (productVariationDto == null) {
			return null;
		}
		return ProductVariation.builder()
				.color(Color.valueOf(productVariationDto.getColor()))
				.size(Size.valueOf(productVariationDto.getSize()))
				.build();
	}

	static ProductVariationDto map(final ProductVariation productVariation) {
		if (productVariation == null) {
			return null;
		}

		return ProductVariationDto.builder()
				.color(String.valueOf(productVariation.getColor()))
				.size(String.valueOf(productVariation.getSize()))
				.build();
	}
}













