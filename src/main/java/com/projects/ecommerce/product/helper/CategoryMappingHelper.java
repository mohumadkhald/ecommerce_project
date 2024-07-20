package com.projects.ecommerce.product.helper;


import com.projects.ecommerce.product.domain.Category;
import com.projects.ecommerce.product.dto.CategoryDto;


public interface CategoryMappingHelper {

	public static CategoryDto map(final Category category) {


		return CategoryDto.builder()
				.categoryId(category.getCategoryId())
				.categoryTitle(category.getCategoryTitle())
				.description(category.getDescription())
				.img(category.getImg())
				.build();
	}

	public static Category map(final CategoryDto categoryDto) {


		return Category.builder()
				.categoryId(categoryDto.getCategoryId())
				.categoryTitle(categoryDto.getCategoryTitle())
				.description(categoryDto.getDescription())
				.img(categoryDto.getImg())
				.build();
	}



}










