package com.projects.ecommerce.product.repository;


import com.projects.ecommerce.product.domain.Category;
import com.projects.ecommerce.product.domain.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Integer> {


    boolean existsByName(String name);
}
