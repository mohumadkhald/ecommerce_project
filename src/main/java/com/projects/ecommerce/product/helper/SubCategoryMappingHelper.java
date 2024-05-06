package com.projects.ecommerce.product.helper;


import com.projects.ecommerce.product.domain.Category;
import com.projects.ecommerce.product.domain.SubCategory;
import com.projects.ecommerce.product.dto.CategoryDto;
import com.projects.ecommerce.product.dto.SubCategoryDto;


public interface SubCategoryMappingHelper {

	public static SubCategoryDto map(final SubCategory subCategory) {
		return SubCategoryDto.builder()
				.id(subCategory.getSubId())
				.name(subCategory.getName())
				.categoryId(subCategory.getCategory() != null ? subCategory.getCategory().getCategoryId() : null)
				.build();
	}


	public static SubCategory map(final SubCategoryDto subCategoryDto) {

		return SubCategory.builder()
				.subId(subCategoryDto.getId())
				.name(subCategoryDto.getName())
				.category(
						Category.builder()
								.categoryId(subCategoryDto.getCategoryId())
								.build())
				.build();
	}



}










