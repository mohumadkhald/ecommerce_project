package com.projects.ecommerce.product.service.impl;


import com.projects.ecommerce.product.domain.Category;
import com.projects.ecommerce.product.dto.SubCategoryDto;
import com.projects.ecommerce.product.exception.wrapper.CategoryNotFoundException;
import com.projects.ecommerce.product.helper.SubCategoryMappingHelper;
import com.projects.ecommerce.product.repository.CategoryRepository;
import com.projects.ecommerce.product.repository.SubCategoryRepository;
import com.projects.ecommerce.product.service.SubCategoryService;
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
public class SubCategoryServiceImpl implements SubCategoryService {
	
	private final SubCategoryRepository subcategoryRepository;
	private final CategoryRepository categoryRepository;

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
	public List<SubCategoryDto> findAllByCategoryTitle(String categoryTitle) {
		log.info("*** SubCategoryDto List, service; fetch all sub-categories by category title *");
		Category category = categoryRepository.findByCategoryTitle(categoryTitle);
		return this.subcategoryRepository.findByCategory(category);
	}

	@Override
	public SubCategoryDto findById(final Integer subCategoryId) {
		log.info("*** CategoryDto, service; fetch category by id *");
		return this.subcategoryRepository.findById(subCategoryId)
				.map(SubCategoryMappingHelper::map)
				.orElseThrow(() -> new CategoryNotFoundException(String.format("Category with id: %d not found", subCategoryId)));
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
		return SubCategoryMappingHelper.map(this.subcategoryRepository
				.save(SubCategoryMappingHelper.map(this.findById(subCategoryId))));
	}
	
	@Override
	public void deleteById(final Integer subCategoryId) {
		log.info("*** Void, service; delete category by id *");
		this.subcategoryRepository.deleteById(subCategoryId);
	}




}









