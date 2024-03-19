package com.projects.ecommerce.repo;

import com.projects.ecommerce.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepo extends JpaRepository<Review, Long> {
    @Query("select rv from Review rv where rv.product.id=:productId")
    public List<Review> getAllProductsReview(@Param("productId") Long productId);
}
