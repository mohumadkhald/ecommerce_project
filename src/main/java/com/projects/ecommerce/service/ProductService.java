package com.projects.ecommerce.service;

import com.projects.ecommerce.exception.ProductException;
import com.projects.ecommerce.model.Category;
import com.projects.ecommerce.model.Product;
import com.projects.ecommerce.requests.CreateProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import org.springframework.data.domain.Pageable;

@Service
public interface ProductService {
    Product createProduct(CreateProductRequest request);

    String deleteProduct(Long id) throws ProductException;

    Product updateProduct(Long id, CreateProductRequest request) throws ProductException;

    Product findProductById(Long id) throws ProductException;


    List<Product> getAllProducts();

    Page<Product> getProductsByCategoryNameAndFilters(String categoryName, String color, Double minPrice, Double maxPrice, int page, int pageSize, Sort sort);

//    Page<Product> getProductsByCategoryName(String categoryName, int page, int pageSize);

}