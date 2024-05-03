package com.projects.ecommerce.product.repository;


import com.projects.ecommerce.product.domain.Color;
import com.projects.ecommerce.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface ProductRepository extends JpaRepository<Product, Integer> {

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

    @Query("SELECT DISTINCT p FROM Product p " +
            "JOIN p.category c " +
            "JOIN p.variations pv " +
            "WHERE c.categoryTitle = :categoryName " +
            "AND (:color IS NULL OR pv.color = :color) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> findByCategoryNameAndFilters(@Param("categoryName") String categoryName,
                                               @Param("color") Color color,
                                               @Param("minPrice") Double minPrice,
                                               @Param("maxPrice") Double maxPrice,
                                               Pageable pageable);



}
