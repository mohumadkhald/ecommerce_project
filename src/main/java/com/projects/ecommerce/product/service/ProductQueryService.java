package com.projects.ecommerce.product.service;

import com.projects.ecommerce.product.dto.AllDetailsProductDto;
import com.projects.ecommerce.product.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.nio.file.AccessDeniedException;
import java.util.List;

// ---------- Interfaces ----------
public interface ProductQueryService {
    Page<ProductDto> getProductsByCategoryNameAndFilters(String subCategoryName, String email, List<String> colors, Double minPrice, Double maxPrice, List<String> sizes, Boolean available, int page, int pageSize, Sort sort);
    Page<ProductDto> getProductsByCategoryNameAndProdcutNameAndFilters(String category, String productName, List<String> colors, Double minPrice, Double maxPrice, List<String> sizes, Boolean available, int page, int pageSize, Sort sort);
    Page<AllDetailsProductDto> findAll(Pageable pageable, Double minPrice, Double maxPrice, List<String> colors, List<String> sizes, Boolean available, String email, String subCat, String productTitle);
    ProductDto findById(Integer productId);
    AllDetailsProductDto findByProductId(String email, int productId) throws AccessDeniedException, AccessDeniedException;
    List<ProductDto> findAllByCreatedBy(String email);
    List<ProductDto> findAllProductsByCreatedBy(String email);
    List<String> getAllEmailSellers(Integer subId);
    List<ProductDto> getSuggestionProductsBySubCategory(Integer subId);
}
