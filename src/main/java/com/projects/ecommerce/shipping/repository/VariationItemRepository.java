package com.projects.ecommerce.shipping.repository;


import com.projects.ecommerce.product.domain.ProductVariation;
import com.projects.ecommerce.shipping.domain.ItemVariation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariationItemRepository extends JpaRepository<ItemVariation, Integer> {
	
	
	
}
