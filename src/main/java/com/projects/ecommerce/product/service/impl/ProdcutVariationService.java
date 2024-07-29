package com.projects.ecommerce.product.service.impl;

import com.projects.ecommerce.product.domain.Color;
import com.projects.ecommerce.product.domain.ProductVariation;
import com.projects.ecommerce.product.domain.Size;
import com.projects.ecommerce.product.repository.ProductVariationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProdcutVariationService {
    private final ProductVariationRepository productVariationRepository;
    public ProductVariation findByProductIdAndColorAndSize(Integer productId, Color color, String size) {
        return productVariationRepository.findByProductIdAndColorAndSize(productId, color, Size.valueOf(size));
    }

    public void save(ProductVariation newProductVariation) {
        productVariationRepository.save(newProductVariation);
    }
}
