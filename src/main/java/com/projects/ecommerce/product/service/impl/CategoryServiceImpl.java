package com.projects.ecommerce.product.service.impl;


import com.projects.ecommerce.product.domain.Category;
import com.projects.ecommerce.product.dto.CategoryDto;
import com.projects.ecommerce.product.exception.wrapper.CategoryNotFoundException;
import com.projects.ecommerce.product.helper.CategoryMappingHelper;
import com.projects.ecommerce.product.repository.CategoryRepository;
import com.projects.ecommerce.product.service.CategoryService;
import com.projects.ecommerce.user.expetion.AlreadyExistsException;
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
		if (categoryRepository.existsByCategoryTitle(categoryRequestDto.getCategoryTitle())) {
			throw new AlreadyExistsException("Category", "Already exists");
		}
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
		log.info("*** CategoryDto, service; update category with categoryId ***");

		try {
			// Retrieve the category by id
			Category category = categoryRepository.findById(categoryId)
					.orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));

			// Check if the category title is being updated
			if (!category.getCategoryTitle().equals(categoryDto.getCategoryTitle())) {
				// If the category title is being updated, check if the new title already exists
				Category existingCategoryByTitle = categoryRepository.findByCategoryTitle(categoryDto.getCategoryTitle());
				if (existingCategoryByTitle != null && !existingCategoryByTitle.getCategoryId().equals(categoryId)) {
					// If the new title already exists and belongs to a different category, throw an exception
					throw new AlreadyExistsException("Category", "Already Exists: " + categoryDto.getCategoryTitle());
				}
			}

			// Map and save the updated category
			category.setCategoryTitle(categoryDto.getCategoryTitle());
			categoryRepository.save(category);
			return CategoryMappingHelper.map(category);
		} catch (CategoryNotFoundException e) {
            log.error("CategoryNotFoundException: {}", e.getMessage());
			throw new CategoryNotFoundException("Category not Found: " + categoryDto.getCategoryTitle());
		} catch (AlreadyExistsException e) {
            log.error("AlreadyExistsException: {}", e.getMessage());
			throw new AlreadyExistsException("Category", "Already Exists: " + categoryDto.getCategoryTitle());
		} catch (Exception e) {
            log.error("An error occurred while updating the category with id {}: {}", categoryId, e.getMessage());
			throw new RuntimeException("Failed to update category", e); // Wrap and re-throw the exception
		}
	}

	
	@Override
	public void deleteById(final Integer categoryId) {
		log.info("*** Void, service; delete category by id *");
		this.categoryRepository.deleteById(categoryId);
	}
	
	
	
}









