package com.projects.ecommerce.product.service;


import com.projects.ecommerce.product.domain.Product;
import com.projects.ecommerce.product.dto.ProductDto;
import com.projects.ecommerce.product.dto.ProductRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ProductService {
	
	List<ProductDto> findAll();
	ProductDto findById(final Integer productId);
	ProductDto save(final ProductRequestDto productDto);
	ProductDto update(final ProductDto productDto);
	ProductDto update(final Integer productId, final ProductDto productDto);
	void deleteById(final Integer productId);

	Page<Product> getProductsByCategoryNameAndFilters(String categoryName, String color, Double minPrice, Double maxPrice, int page, int pageSize, Sort sort);
}
