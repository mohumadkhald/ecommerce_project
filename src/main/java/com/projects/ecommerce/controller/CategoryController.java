package com.projects.ecommerce.controller;

import com.projects.ecommerce.model.Category;
import com.projects.ecommerce.requests.CategoryRequest;
import com.projects.ecommerce.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody CategoryRequest categoryRequest) {

        Category newCategory = categoryService.createCategory(categoryRequest.getName());
        return ResponseEntity.ok(newCategory);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/find")
    public ResponseEntity<Category> findCategoryByName(
            @RequestParam("name") String name) {

        Category category = categoryService.findCategoryByName(name);

        if (category != null) {
            return ResponseEntity.ok(category);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
