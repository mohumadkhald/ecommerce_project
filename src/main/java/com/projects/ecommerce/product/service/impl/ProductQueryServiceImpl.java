package com.projects.ecommerce.product.service.impl;

import com.projects.ecommerce.product.domain.Product;
import com.projects.ecommerce.product.dto.AllDetailsProductDto;
import com.projects.ecommerce.product.dto.ProductDto;
import com.projects.ecommerce.product.helper.ProductMappingHelper;
import com.projects.ecommerce.product.repository.ProductRepository;
import com.projects.ecommerce.product.service.CategoryService;
import com.projects.ecommerce.product.service.SubCategoryService;
import com.projects.ecommerce.user.expetion.NotFoundException;
import com.projects.ecommerce.user.model.Role;
import com.projects.ecommerce.user.repository.UserRepo;
import com.projects.ecommerce.product.service.ProductQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductQueryServiceImpl implements ProductQueryService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final SubCategoryService subCategoryService;
    private final UserRepo userRepository; // for admin checks in findByProductId

    // Delegate to the same filtering logic used originally
    @Override
    public Page<ProductDto> getProductsByCategoryNameAndFilters(String subCategoryName, String email, List<String> colors, Double minPrice, Double maxPrice, List<String> sizes, Boolean available, int page, int pageSize, Sort sort) {
        // Reuse same method signature as original
        ProductServiceImplHelper helper = new ProductServiceImplHelper(productRepository, categoryService, subCategoryService);
        return helper.getFilteredProducts(subCategoryName, email, null, colors, minPrice, maxPrice, sizes, available, page, pageSize, sort).map(ProductMappingHelper::map);
    }

    @Override
    public Page<ProductDto> getProductsByCategoryNameAndProdcutNameAndFilters(String category, String productName, List<String> colors, Double minPrice, Double maxPrice, List<String> sizes, Boolean available, int page, int pageSize, Sort sort) {
        ProductServiceImplHelper helper = new ProductServiceImplHelper(productRepository, categoryService, subCategoryService);
        return helper.getFilteredProductsByName(category, null, productName, colors, minPrice, maxPrice, sizes, available, page, pageSize, sort).map(ProductMappingHelper::map);
    }

    @Override
    public Page<AllDetailsProductDto> findAll(Pageable pageable, Double minPrice, Double maxPrice, List<String> colors, List<String> sizes, Boolean available, String email, String subCat, String productTitle) {
        ProductServiceImplHelper helper = new ProductServiceImplHelper(productRepository, categoryService, subCategoryService);
        return helper.getFilteredProductsAll(subCat, email, productTitle, colors, minPrice, maxPrice, sizes, available, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()).map(ProductMappingHelper::map2);
    }

    @Override
    public ProductDto findById(Integer productId) {
        return productRepository.findById(productId).map(ProductMappingHelper::map)
                .orElseThrow(() -> new NotFoundException("Product", "Product with ID " + productId + " Not Found: "));
    }

    @Override
    public AllDetailsProductDto findByProductId(String email, int productId) throws AccessDeniedException {
        return getAllDetailsProductDto(email, productId, productRepository, userRepository);
    }

    static AllDetailsProductDto getAllDetailsProductDto(String email, int productId, ProductRepository productRepository, UserRepo userRepository) throws AccessDeniedException {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("product", "Product not found with id: " + productId));
        boolean isAdmin = userRepository.findAllByRole(Role.ADMIN).stream().anyMatch(user -> user.getEmail().equals(email));
        if (isAdmin || product.getCreatedBy().equals(email)) {
            return productRepository.findById(productId).map(ProductMappingHelper::map2).orElseThrow(() -> new NotFoundException("product", "Product not found with id: " + productId));
        } else {
            throw new AccessDeniedException("You do not have permission to access this product.");
        }
    }

    @Override
    public List<ProductDto> findAllByCreatedBy(String email) {
        return productRepository.findAllByCreatedBy(email).stream().map(ProductMappingHelper::map).collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> findAllProductsByCreatedBy(String email) {
        return findAllByCreatedBy(email);
    }

    @Override
    public List<String> getAllEmailSellers(Integer subId) {
        List<Product> products = (subId != 0) ? productRepository.findBySubcategoryId(subId) : productRepository.findAll();
        return products.stream().map(Product::getCreatedBy).distinct().collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> getSuggestionProductsBySubCategory(Integer subId) {
        List<Product> products = productRepository.findBySubcategoryId(subId);
        if (products.isEmpty()) return Collections.emptyList();
        Collections.shuffle(products);
        return products.stream().limit(10).map(ProductMappingHelper::map).collect(Collectors.toList());
    }
}
