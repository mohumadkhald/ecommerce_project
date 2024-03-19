package com.projects.ecommerce.service;

import com.projects.ecommerce.model.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(String name);
    List<Category> getAllCategories();

    Category getCategoryById(Long parentCategoryId);

    Category findCategoryByName(String name);
}
