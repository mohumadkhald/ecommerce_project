package com.projects.ecommerce.product.service;


import com.projects.ecommerce.product.domain.Product;
import com.projects.ecommerce.product.dto.ProductDto;
import com.projects.ecommerce.product.dto.ProductRequestDto;
import com.projects.ecommerce.product.dto.Spec;
import com.projects.ecommerce.product.dto.SubCategoryDto;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ProductService {

	Page<ProductDto> findAll(Pageable pageable);
	ProductDto findById(final Integer productId);
	ProductDto create(final ProductRequestDto productDto);
	List<ProductDto> saveAll(List<ProductRequestDto> productDtos);
	ProductDto update(final ProductDto productDto);
	ProductDto update(final Integer productId, final ProductRequestDto productDto);
	void deleteById(final Integer productId);
	Page<ProductDto> getProductsByCategoryNameAndFilters(String categoryName, String color, Double minPrice, Double maxPrice, String size, int page, int pageSize, Sort sort);


	Map<String, Map<String, Map<String, Integer>>> getProductVariations(String productName);

	@Transactional
	void updateProductVariation(Integer productId, Spec spec);

	@Transactional
	void updateProductStocks(Integer productId, List<Spec> specs, boolean increaseQuantity);



	Product findProductById(Integer productId);


	@Transactional
	void updateProductStock(Integer productId, List<Spec> specs, Integer quantityToSubtract);

    Page<ProductDto> getProductsByCategoryNameAndProdcutNameAndFilters(String subCategoryName, String productNmae, String color, Double minPrice, Double maxPrice, String s, int page, int pageSize, Sort sort);

	List<ProductDto> findAllByCreatedBy(String email);

	List<ProductDto> findAllProductsByCreatedBy(String email);

    void removeProductByCreatedBy(String email, Integer productId);
}
