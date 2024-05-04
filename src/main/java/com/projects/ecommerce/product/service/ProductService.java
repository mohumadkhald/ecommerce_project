package com.projects.ecommerce.product.service;


import com.projects.ecommerce.product.domain.Size;
import com.projects.ecommerce.product.dto.ProductDto;
import com.projects.ecommerce.product.dto.ProductRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ProductService {

	Page<ProductDto> findAll(Pageable pageable);
	ProductDto findById(final Integer productId);
	ProductDto create(final ProductRequestDto productDto);
	List<ProductDto> saveAll(List<ProductRequestDto> productDtos);
	ProductDto update(final ProductDto productDto);
	ProductDto update(final Integer productId, final ProductDto productDto);
	void deleteById(final Integer productId);
	Page<ProductDto> getProductsByCategoryNameAndFilters(String categoryName, String color, Double minPrice, Double maxPrice, String size, int page, int pageSize, Sort sort);
}
