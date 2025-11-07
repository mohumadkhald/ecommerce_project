package com.projects.ecommerce.order.repostiory;


import com.projects.ecommerce.order.model.OrderVariation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderVariationRepository extends JpaRepository<OrderVariation, Integer> {

}
