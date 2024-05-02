package com.projects.ecommerce.product.service;


import com.projects.ecommerce.product.dto.CategoryDto;
import com.projects.ecommerce.product.dto.CategoryRequestDto;

import java.util.List;

public interface CategoryService {
	
	List<CategoryDto> findAll();
	CategoryDto findById(final Integer categoryId);
	CategoryDto save(final CategoryDto categoryRequestDto);
	CategoryDto update(final CategoryDto categoryDto);
	CategoryDto update(final Integer categoryId, final CategoryDto categoryDto);
	void deleteById(final Integer categoryId);
	
}
