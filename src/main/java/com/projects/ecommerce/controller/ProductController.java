package com.projects.ecommerce.controller;

import com.projects.ecommerce.exception.ProductException;
import com.projects.ecommerce.model.Product;
import com.projects.ecommerce.requests.CreateProductRequest;
import com.projects.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/create")
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest request) {
        // Implement logic to create a product and return a ResponseEntity with the created product
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long id) {
        // Implement logic to delete a product by its ID and return a ResponseEntity with a message
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Product deleted successfully");
        } catch (ProductException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") Long id, @RequestBody CreateProductRequest request) {
        // Implement logic to update a product by its ID and return a ResponseEntity with the updated product
        try {
            Product updatedProduct = productService.updateProduct(id, request);
            return ResponseEntity.ok(updatedProduct);
        } catch (ProductException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findProductById(@PathVariable("id") Long id) {
        // Implement logic to find a product by its ID and return a ResponseEntity with the found product
        try {
            Product product = productService.findProductById(id);
            return ResponseEntity.ok(product);
        } catch (ProductException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }



    @GetMapping("/bycategory/{categoryName}")
    public ResponseEntity<Page<Product>> getProductsByCategoryNameAndFilters(
            @PathVariable String categoryName,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort sort = Sort.by("createdAt").descending();
        if (!sortDirection.equals("desc")) {
            sort = Sort.by("createdAt").ascending();
        }

        Page<Product> products = productService.getProductsByCategoryNameAndFilters(categoryName, color, minPrice, maxPrice, page, pageSize, sort);
        return ResponseEntity.ok(products);
    }

}



