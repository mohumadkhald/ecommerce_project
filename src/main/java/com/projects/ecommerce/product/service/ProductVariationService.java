package com.projects.ecommerce.product.service;

import com.projects.ecommerce.product.dto.Spec;

import java.util.List;
import java.util.Map;

public interface ProductVariationService {
    Map<String, Map<String, Map<String, Integer>>> getProductVariations(String productName);
    void updateProductVariation(Integer productId, List<Spec> specs, List<String> imageUrls);

}