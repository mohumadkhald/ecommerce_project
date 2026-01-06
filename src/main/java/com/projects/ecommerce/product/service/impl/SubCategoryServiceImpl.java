package com.projects.ecommerce.product.service.impl;


import com.projects.ecommerce.product.domain.Category;
import com.projects.ecommerce.product.domain.SubCategory;
import com.projects.ecommerce.product.dto.CategoryDto;
import com.projects.ecommerce.product.dto.SubCategoryDto;
import com.projects.ecommerce.product.helper.CategoryMappingHelper;
import com.projects.ecommerce.product.helper.SubCategoryMappingHelper;
import com.projects.ecommerce.product.repository.CategoryRepository;
import com.projects.ecommerce.product.repository.SubCategoryRepository;
import com.projects.ecommerce.product.service.SubCategoryService;
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
public class SubCategoryServiceImpl implements SubCategoryService {
	
	private final SubCategoryRepository subcategoryRepository;
	private final CategoryRepository categoryRepository;
	private final SubCategoryRepository subCategoryRepository;

	@Override
	public List<SubCategoryDto> findAll() {
		log.info("*** CategoryDto List, service; fetch all categorys *");
		return this.subcategoryRepository.findAll()
				.stream()
					.map(SubCategoryMappingHelper::map)
					.distinct()
					.toList();
	}

	@Override
	public CategoryDto findAllByCategoryTitle(String categoryTitle) {
		log.info("*** SubCategoryDto List, service; fetch all sub-categories by category title *");
		Category category = categoryRepository.findByCategoryTitle(categoryTitle);
		return CategoryMappingHelper.map(category);
	}

	@Override
	public boolean findByName(String categoryName) {
		return subcategoryRepository.existsByName(categoryName);
	}

	@Override
	public SubCategoryDto findById(final Integer subCategoryId) {
		log.info("*** CategoryDto, service; fetch category by id *");
		return this.subcategoryRepository.findById(subCategoryId)
				.map(SubCategoryMappingHelper::map)
				.orElseThrow(() -> new NotFoundException("Category", String.format("Category with id: %d not found", subCategoryId)));
	}
	
	@Override
	public SubCategoryDto save(final SubCategoryDto subCategoryDto) {
		log.info("*** CategoryDto, service; save category *");
		if (subcategoryRepository.existsByName(subCategoryDto.getName())) {
			throw new AlreadyExistsException("Sub_Category", "Already Exists");
		}
		return SubCategoryMappingHelper.map(this.subcategoryRepository
				.save(SubCategoryMappingHelper.map(subCategoryDto)));
	}
	
	@Override
	public SubCategoryDto update(final SubCategoryDto subCategoryDto) {
		log.info("*** CategoryDto, service; update category *");
		return SubCategoryMappingHelper.map(this.subcategoryRepository
				.save(SubCategoryMappingHelper.map(subCategoryDto)));
	}
	
	@Override
	public SubCategoryDto update(final Integer subCategoryId, final SubCategoryDto subCategoryDto) {
		log.info("*** CategoryDto, service; update category with categoryId *");
		Optional<SubCategory> subCategoryOptional = subCategoryRepository.findById(subCategoryId);
		if (subCategoryOptional.isPresent())
		{
			SubCategory subCategory = subCategoryOptional.get();
			if (!subCategory.getName().equals(subCategoryDto.getName()) &&
					subCategoryRepository.existsByName(subCategoryDto.getName())) {
				throw new AlreadyExistsException("Sub_Category", "Already exists");
			}
			subCategory.setCategory(Category.builder().categoryId(subCategoryDto.getCategoryId()).build());
			subCategory.setImg(subCategoryDto.getImg());
			subCategory.setName(subCategoryDto.getName());
			subCategoryRepository.save(subCategory);
			return SubCategoryMappingHelper.map(subCategory);
		} else {
			throw new NotFoundException("Sub_Category", "Not Found");
		}
	}
	
	@Override
	public void deleteById(final Integer subCategoryId) {
		log.info("*** Void, service; delete category by id *");
		this.subcategoryRepository.deleteById(subCategoryId);
	}



	@Override
	public long getSubCategoriesCount() {
		return subCategoryRepository.count();
	}



}









