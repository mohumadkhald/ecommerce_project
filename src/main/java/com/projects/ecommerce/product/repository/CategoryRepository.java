package com.projects.ecommerce.product.repository;


import com.projects.ecommerce.product.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {


    boolean existsByCategoryTitle(String categoryTitle);

    Category findByCategoryTitle(String categoryTitle);

}
