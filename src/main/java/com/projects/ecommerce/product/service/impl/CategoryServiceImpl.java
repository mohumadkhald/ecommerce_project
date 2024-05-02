package com.projects.ecommerce.product.service.impl;


import com.projects.ecommerce.product.dto.CategoryDto;
import com.projects.ecommerce.product.dto.CategoryRequestDto;
import com.projects.ecommerce.product.exception.wrapper.CategoryNotFoundException;
import com.projects.ecommerce.product.helper.CategoryMappingHelper;
import com.projects.ecommerce.product.repository.CategoryRepository;
import com.projects.ecommerce.product.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
	
	private final CategoryRepository categoryRepository;
	
	@Override
	public List<CategoryDto> findAll() {
		log.info("*** CategoryDto List, service; fetch all categorys *");
		return this.categoryRepository.findAll()
				.stream()
					.map(CategoryMappingHelper::map)
					.distinct()
					.toList();
	}
	
	@Override
	public CategoryDto findById(final Integer categoryId) {
		log.info("*** CategoryDto, service; fetch category by id *");
		return this.categoryRepository.findById(categoryId)
				.map(CategoryMappingHelper::map)
				.orElseThrow(() -> new CategoryNotFoundException(String.format("Category with id: %d not found", categoryId)));
	}
	
	@Override
	public CategoryDto save(final CategoryDto categoryRequestDto) {
		log.info("*** CategoryDto, service; save category *");
		return CategoryMappingHelper.map(this.categoryRepository
				.save(CategoryMappingHelper.map(categoryRequestDto)));
	}
	
	@Override
	public CategoryDto update(final CategoryDto categoryDto) {
		log.info("*** CategoryDto, service; update category *");
		return CategoryMappingHelper.map(this.categoryRepository
				.save(CategoryMappingHelper.map(categoryDto)));
	}
	
	@Override
	public CategoryDto update(final Integer categoryId, final CategoryDto categoryDto) {
		log.info("*** CategoryDto, service; update category with categoryId *");
		return CategoryMappingHelper.map(this.categoryRepository
				.save(CategoryMappingHelper.map(this.findById(categoryId))));
	}
	
	@Override
	public void deleteById(final Integer categoryId) {
		log.info("*** Void, service; delete category by id *");
		this.categoryRepository.deleteById(categoryId);
	}
	
	
	
}









