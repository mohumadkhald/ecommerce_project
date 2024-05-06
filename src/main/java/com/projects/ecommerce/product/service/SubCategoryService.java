package com.projects.ecommerce.product.service;


import com.projects.ecommerce.product.dto.CategoryDto;
import com.projects.ecommerce.product.dto.SubCategoryDto;

import java.util.List;

public interface SubCategoryService {
	
	List<SubCategoryDto> findAll();
	SubCategoryDto findById(final Integer subCategoryId);
	SubCategoryDto save(final SubCategoryDto subCategoryDto);
	SubCategoryDto update(final SubCategoryDto subCategoryDto);
	SubCategoryDto update(final Integer subCategoryId, final SubCategoryDto subCategoryDto);
	void deleteById(final Integer subCategoryId);
	
}
