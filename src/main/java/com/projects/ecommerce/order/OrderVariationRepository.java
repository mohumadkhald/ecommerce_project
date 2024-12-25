package com.projects.ecommerce.order;


import com.projects.ecommerce.product.domain.Color;
import com.projects.ecommerce.product.domain.ProductVariation;
import com.projects.ecommerce.product.domain.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderVariationRepository extends JpaRepository<OrderVariation, Integer> {

}
