package com.projects.ecommerce.service;

import com.projects.ecommerce.exception.ProductException;
import com.projects.ecommerce.model.Category;
import com.projects.ecommerce.model.Product;
import com.projects.ecommerce.repo.CategoryRepo;
import com.projects.ecommerce.repo.ProductRepo;
import com.projects.ecommerce.requests.CreateProductRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final CategoryService categoryService;


    @Override
    public Product createProduct(CreateProductRequest request) {
        // Calculate discounted price
        int discountedPrice = calculateDiscountedPrice(request.getPrice(), request.getDiscountPercent());

        // Fetch the category from the database based on the category ID
        Optional<Category> categoryOptional = categoryRepo.findById((long) request.getCategoryId());
        if (categoryOptional.isEmpty()) {
            // Handle the case where the category with the provided ID does not exist
            throw new IllegalArgumentException("Category with ID " + request.getCategoryId() + " not found");
        }
        Category category = categoryOptional.get();

        // Create a new Product object
        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountedPrice(discountedPrice);
        product.setDiscountPercent(request.getDiscountPercent());
        product.setQuantity(request.getQuantity());
        product.setBrand(request.getBrand());
        product.setColor(request.getColor());
        product.setSize(request.getSize());
        product.setCategory(category); // Set the fetched category
        product.setImageUrl(request.getImageUrl());
        product.setCreatedAt(LocalDateTime.now());

        // Save the product to the repository
        return productRepo.save(product);
    }

    private int calculateDiscountedPrice(int price, int discountPercent) {
        // Calculate the discounted price
        return price - (price * discountPercent / 100);
    }
    @Override
    public String deleteProduct(Long id) throws ProductException {
        Product product = findProductById(id);
        productRepo.delete(product);
        return "Product deleted successfully";
    }

    @Override
    public Product updateProduct(Long id, CreateProductRequest request) throws ProductException {
        Product product = findProductById(id);

        // Update product fields if provided
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPercent(request.getDiscountPercent());
        product.setQuantity(request.getQuantity());
        product.setBrand(request.getBrand());
        product.setColor(request.getColor());
        product.setSize(request.getSize());
        product.setImageUrl(request.getImageUrl());
        product.setDiscountedPrice(calculateDiscountedPrice(request.getPrice(), request.getDiscountPercent()));
        product.setUpdatedAt(LocalDateTime.now());

        // Fetch the category from the database based on the category ID
        Optional<Category> categoryOptional = categoryRepo.findById((long) request.getCategoryId());
        if (categoryOptional.isEmpty()) {
            // Handle the case where the category with the provided ID does not exist
            throw new IllegalArgumentException("Category with ID " + request.getCategoryId() + " not found");
        }
        Category category = categoryOptional.get();

        // Set the fetched category to the product
        product.setCategory(category);

        // Save the updated product to the repository
        return productRepo.save(product);
    }

    @Override
    public Product findProductById(Long id) throws ProductException {
        Optional<Product> optional = productRepo.findById(id);
        return optional.orElseThrow(() -> new ProductException("Product not found"));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }




    public Page<Product> getProductsByCategoryName(String categoryName, int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return productRepo.findByCategoryName(categoryName, pageRequest);
    }
}