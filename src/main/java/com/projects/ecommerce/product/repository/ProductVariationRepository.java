package com.projects.ecommerce.product.repository;


import com.projects.ecommerce.product.domain.Color;
import com.projects.ecommerce.product.domain.ProductVariation;
import com.projects.ecommerce.product.domain.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariationRepository extends JpaRepository<ProductVariation, Integer> {


    ProductVariation findByProductIdAndColorAndSize(Integer product_id, Color color, Size size);
}
