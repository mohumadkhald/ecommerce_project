package com.projects.ecommerce.product.repository;


import com.projects.ecommerce.product.domain.Category;
import com.projects.ecommerce.product.domain.ProductVariation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariationProductRepository extends JpaRepository<ProductVariation, Integer> {
	
	
	
}
