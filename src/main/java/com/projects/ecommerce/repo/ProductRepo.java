package com.projects.ecommerce.repo;

import com.projects.ecommerce.model.Category;
import com.projects.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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


    public List<Product> filterProducts(
            @Param("category") String category,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("minDiscount") Integer minDiscount,
            @Param("sort") String sort);

    // page and size only
//    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.name = :categoryName")
//    Page<Product> findByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);






//    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.name = :categoryName " +
//            "AND (:color IS NULL OR p.color = :color) " +
//            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
//            "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
//    Page<Product> findByCategoryNameAndFilters(@Param("categoryName") String categoryName,
//                                               @Param("color") String color,
//                                               @Param("minPrice") Double minPrice,
//                                               @Param("maxPrice") Double maxPrice,
//                                               Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.name = :categoryName " +
            "AND (:color IS NULL OR p.color = :color) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> findByCategoryNameAndFilters(@Param("categoryName") String categoryName,
                                               @Param("color") String color,
                                               @Param("minPrice") Double minPrice,
                                               @Param("maxPrice") Double maxPrice,
                                               Pageable pageable);


}
