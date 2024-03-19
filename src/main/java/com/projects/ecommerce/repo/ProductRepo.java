package com.projects.ecommerce.repo;

import com.projects.ecommerce.model.Category;
import com.projects.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
@EnableJpaRepositories
public interface ProductRepo extends JpaRepository<Product, Long> {
    @Query("select p from Product p " +
            "where (p.category.name = :category or :category = '') " +
            "and ((:minPrice is null and :maxPrice is null) or (p.discountedPrice between :minPrice and :maxPrice)) " +
            "and (:minDiscount is null or p.discountPercent >= :minDiscount) " +
            "order by " +
            "case when :sort = 'price_low' then p.discountedPrice end asc, " +
            "case when :sort = 'price_high' then p.discountedPrice end desc"
    )

//    public List<Product> filterProducts(@Param("category") String category,
//                                        @Param("minPrice") Integer minPrice, @Param("minPrice") Integer maxPrice,
//                                        @Param("minDiscounted") Integer minDiscounted,
//                                        @Param("sort") String sort);
    public List<Product> filterProducts(
            @Param("category") String category,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("minDiscount") Integer minDiscount,
            @Param("sort") String sort);
    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.name = :categoryName")
    List<Product> findByCategoryName(@Param("categoryName") String categoryName);



}
