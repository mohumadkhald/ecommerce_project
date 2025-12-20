package com.projects.ecommerce.product.helper;


import com.projects.ecommerce.product.domain.Category;
import com.projects.ecommerce.product.domain.SubCategory;
import com.projects.ecommerce.product.dto.CategoryDto;
import com.projects.ecommerce.product.dto.SubCategoryDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface CategoryMappingHelper {

	public static CategoryDto map(final Category category) {
		return CategoryDto.builder()
				.categoryId(category.getCategoryId())
				.categoryTitle(category.getCategoryTitle())
				.description(category.getDescription())
				.img(category.getImg())
				.subCategoryDtos(
						category.getSubCategories().stream()
								.map(sub -> SubCategoryDto.builder()
										.id(sub.getSubId())
										.name(sub.getName())
										.img(sub.getImg())
										.description(sub.getDescription())
										.categoryId(category.getCategoryId())
										.build())
								.collect(Collectors.toList()) // ✅ now it’s a List
				)
				.build();
	}

	public static Category map(final CategoryDto categoryDto) {
		return Category.builder()
				.categoryId(categoryDto.getCategoryId())
				.categoryTitle(categoryDto.getCategoryTitle())
				.description(categoryDto.getDescription())
				.img(categoryDto.getImg())
				.subCategories(
						categoryDto.getSubCategoryDtos().stream()
								.map(sub -> SubCategory.builder()  // <-- probably SubCategory, not SubCategoryDto
										.subId(sub.getId())
										.name(sub.getName())
										.img(sub.getImg())
										.build())
								.collect(Collectors.toSet()) // <-- fix here
				)
				.build();
	}
}










