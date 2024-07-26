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

	Page<ProductDto> findAll(Pageable pageable, Double minPrice, Double maxPrice);

	ProductDto findById(final Integer productId);
	ProductDto create(final ProductRequestDto productDto);
	List<ProductDto> saveAll(List<ProductRequestDto> productDtos);
	ProductDto update(final ProductDto productDto);
	ProductDto update(final Integer productId, final ProductRequestDto productDto);
	void deleteById(final Integer productId);
	Page<ProductDto> getProductsByCategoryNameAndFilters
			(String categoryName, List<String> colors,
			 Double minPrice, Double maxPrice, List<String> sizes,
			 int page, int pageSize, Sort sort);


	Map<String, Map<String, Map<String, Integer>>> getProductVariations(String productName);

	@Transactional
	void updateProductVariation(Integer productId, List<Spec> spec);

	@Transactional
	void updateProductStocks(Integer productId, List<Spec> specs, boolean increaseQuantity);



	Product findProductById(Integer productId);


	@Transactional
	void updateProductStock(Integer productId, List<Spec> specs, Integer quantityToSubtract);

    Page<ProductDto> getProductsByCategoryNameAndProdcutNameAndFilters(String subCategoryName, String productNmae, List<String> color, Double minPrice, Double maxPrice, List<String> size, int page, int pageSize, Sort sort);

	List<ProductDto> findAllByCreatedBy(String email);

	List<ProductDto> findAllProductsByCreatedBy(String email);

    ResponseEntity<?> removeProductByCreatedBy(String email, Integer productId);

	ResponseEntity<?> removeProductsByCreatedBy(String email, List<Integer> productIds);

	AllDetailsProductDto findByProductId(String email, int i) throws AccessDeniedException;

	ResponseEntity<?> setDiscount(String email, Integer productId, Double discount) throws AccessDeniedException;

	ResponseEntity<?> setDiscounts(String email, List<Integer> productIds, Double discount) throws AccessDeniedException;
}
