package com.projects.ecommerce.product.service;


import com.projects.ecommerce.product.domain.Product;
import com.projects.ecommerce.product.dto.AllDetailsProductDto;
import com.projects.ecommerce.product.dto.ProductDto;
import com.projects.ecommerce.product.dto.ProductRequestDto;
import com.projects.ecommerce.product.dto.Spec;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

public interface ProductService {

//	Page<ProductDto> findAll(Pageable pageable);


	Page<ProductDto> getProductsByCategoryNameAndFilters(String subCategoryName, List<String> colors, Double minPrice, Double maxPrice, List<String> sizes, Boolean available, int page, int pageSize, Sort sort);

	Page<ProductDto> getProductsByCategoryNameAndProdcutNameAndFilters(String subCategoryName, String productName, List<String> colors, Double minPrice, Double maxPrice, List<String> sizes, int page, int pageSize, Sort sort);

	Page<AllDetailsProductDto> findAll(Pageable pageable, Double minPrice, Double maxPrice, List<String> colors, List<String> sizes, Boolean available, String email, String productTitle);

	ProductDto findById(final Integer productId);
	ProductDto create(final ProductRequestDto productDto);
	List<ProductDto> saveAll(List<ProductRequestDto> productDtos, String email);
	ProductDto update(final ProductDto productDto);
	ProductDto update(final Integer productId, final ProductRequestDto productDto);
	void deleteById(final Integer productId);


	Map<String, Map<String, Map<String, Integer>>> getProductVariations(String productName);


	@Transactional
	void updateProductVariation(Integer productId, List<Spec> specs, List<String> imageUrls);

	@Transactional
	void updateProductStocks(Integer productId, List<Spec> specs, boolean increaseQuantity);


	Product findProductById(Integer productId);


	@Transactional
	void updateProductStock(Integer productId, List<Spec> specs, Integer quantityToSubtract);

	List<ProductDto> findAllByCreatedBy(String email);

	List<ProductDto> findAllProductsByCreatedBy(String email);

    ResponseEntity<?> removeProductByCreatedBy(String email, Integer productId);

	ResponseEntity<?> removeProductsByCreatedBy(String email, List<Integer> productIds);

	AllDetailsProductDto findByProductId(String email, int i) throws AccessDeniedException;

	ResponseEntity<?> setDiscount(String email, Integer productId, Double discount) throws AccessDeniedException;

	ResponseEntity<?> setDiscounts(String email, List<Integer> productIds, Double discount) throws AccessDeniedException;
}
