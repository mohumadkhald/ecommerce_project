package com.projects.ecommerce.product.service.impl;


import com.projects.ecommerce.product.domain.Category;
import com.projects.ecommerce.product.dto.CategoryDto;
import com.projects.ecommerce.product.exception.wrapper.CategoryNotFoundException;
import com.projects.ecommerce.product.helper.CategoryMappingHelper;
import com.projects.ecommerce.product.repository.CategoryRepository;
import com.projects.ecommerce.product.service.CategoryService;
import com.projects.ecommerce.user.expetion.AlreadyExistsException;
import com.projects.ecommerce.user.expetion.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
	public boolean findByName(String categoryName) {
		return categoryRepository.existsByCategoryTitle(categoryName);
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
		Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
		if (categoryOptional.isPresent())
		{
			Category category = categoryOptional.get();
			// Check if the category title already exists but ignore if it’s the same category being updated
			if (!category.getCategoryTitle().equals(categoryDto.getCategoryTitle()) &&
					categoryRepository.existsByCategoryTitle(categoryDto.getCategoryTitle())) {
				throw new AlreadyExistsException("Category", "Already exists");
			}
			category.setCategoryTitle(categoryDto.getCategoryTitle());
			category.setImg(categoryDto.getImg());
			categoryRepository.save(category);
			return CategoryMappingHelper.map(category);
		} else {
			throw new NotFoundException("Category", "Category Not " + categoryDto.getCategoryTitle() +  "Found");
		}
	}

	
	@Override
	public void deleteById(final Integer categoryId) {
		log.info("*** Void, service; delete category by id *");
		this.categoryRepository.deleteById(categoryId);
	}

	@Override
	public Category findByCategoryName(String categoryName) {
		return categoryRepository.findByCategoryTitle(categoryName);
	}


}









