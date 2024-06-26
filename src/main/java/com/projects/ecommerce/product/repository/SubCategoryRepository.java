package com.projects.ecommerce.product.repository;


import com.projects.ecommerce.product.domain.Category;
import com.projects.ecommerce.product.domain.SubCategory;
import com.projects.ecommerce.product.dto.SubCategoryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Integer> {


    boolean existsByName(String name);


    @Query("SELECT new com.projects.ecommerce.product.dto.SubCategoryDto(s.subId, s.name, s.category.categoryId, s.img) " +
            "FROM SubCategory s WHERE s.category = :category")
    List<SubCategoryDto> findByCategory(@Param("category") Category category);
}
