package com.projects.ecommerce.product.service.impl;

import com.projects.ecommerce.product.domain.Color;
import com.projects.ecommerce.product.domain.Product;
import com.projects.ecommerce.product.domain.ProductVariation;
import com.projects.ecommerce.product.domain.Size;
import com.projects.ecommerce.product.dto.ProductRequestDto;
import com.projects.ecommerce.product.dto.Spec;
import com.projects.ecommerce.product.exception.wrapper.ProductNotFoundException;
import com.projects.ecommerce.product.repository.ProductRepository;
import com.projects.ecommerce.product.service.ProductVariationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.projects.ecommerce.product.service.impl.ProductCommandServiceImpl.newProductVariation;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductVariationServiceImpl implements ProductVariationService {
    private final ProductRepository productRepository;

    @Override
    public Map<String, Map<String, Map<String, Integer>>> getProductVariations(String productName) {
        Product product = productRepository.findByProductTitle(productName);
        if (product == null) return Collections.emptyMap();
        Map<String, Map<String, Integer>> variationsMap = new HashMap<>();
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
    public void updateProductVariation(Integer productId, List<Spec> specs) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        for (int i = 0; i < specs.size(); i++) {
            Spec spec = specs.get(i);
            Optional<ProductVariation> existingVariation = product.getVariations().stream()
                    .filter(v -> v.getSize().equals(Size.valueOf(spec.getSize())) && v.getColor().equals(Color.valueOf(spec.getColor())))
                    .findFirst();
            if (existingVariation.isPresent()) {
                ProductVariation variationToUpdate = existingVariation.get();
                variationToUpdate.setQuantity(spec.getQuantity());
            } else {
                newProductVariation(product, spec, 0);
            }
        }
        int totalQuantity = product.getVariations().stream().mapToInt(ProductVariation::getQuantity).sum();
        product.setAllQuantity(totalQuantity);
        productRepository.save(product);
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
}
