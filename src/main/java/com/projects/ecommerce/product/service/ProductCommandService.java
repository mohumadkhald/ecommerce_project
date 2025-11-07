package com.projects.ecommerce.product.service;

import com.projects.ecommerce.product.domain.Product;
import com.projects.ecommerce.product.dto.ProductDto;
import com.projects.ecommerce.product.dto.ProductEditDto;
import com.projects.ecommerce.product.dto.ProductRequestDto;
import com.projects.ecommerce.product.dto.Spec;
import jakarta.validation.Valid;

import java.util.List;

public interface ProductCommandService {
    ProductDto create(ProductRequestDto productDto);
    List<ProductDto> saveAll(List<ProductRequestDto> productDtos, String email);
    ProductDto update(ProductDto productDto);
    ProductDto update(Integer productId, @Valid ProductEditDto productDto);
    void deleteById(Integer productId);
    void updateProductStocks(Integer productId, List<Spec> specs, boolean increaseQuantity);
    Product findProductById(Integer productId);
    void updateProductStock(Integer productId, List<Spec> specs, Integer quantityToSubtract);
}
