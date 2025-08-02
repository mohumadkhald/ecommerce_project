package com.projects.ecommerce.product.repository;


import com.projects.ecommerce.product.domain.Color;
import com.projects.ecommerce.product.domain.Product;
import com.projects.ecommerce.product.domain.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

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
            "JOIN p.subCategory c " +
            "JOIN p.variations pv " +
            "WHERE c.name = :subCategoryName " +
            "AND (:colors IS NULL OR pv.color IN :colors) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:sizes IS NULL OR pv.size IN :sizes) " +
            "AND (:available IS NULL OR (:available = TRUE AND p.allQuantity > 0) OR (:available = FALSE AND p.allQuantity <= 0))"
    )
    Page<Product> findByCategoryNameAndFilters(@Param("subCategoryName") String subCategoryName,
                                               @Param("colors") List<Color> colors,
                                               @Param("minPrice") Double minPrice,
                                               @Param("maxPrice") Double maxPrice,
                                               @Param("sizes") List<Size> sizes,
                                               @Param("available") Boolean available,
                                               Pageable pageable);




    @Query("SELECT DISTINCT p FROM Product p " +
            "JOIN p.subCategory sc " +
            "JOIN sc.category c " +
            "JOIN p.variations pv " +
            "WHERE (:categoryName = 'all' OR c.categoryTitle = :categoryName) " +
            "AND (:productName IS NULL OR p.productTitle LIKE %:productName%) " +
            "AND (:color IS NULL OR pv.color IN :color) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:size IS NULL OR pv.size IN :size)")
    Page<Product> findByCategoryNameAndProductTitleAndFilters(
            @Param("categoryName") String categoryName,
            @Param("productName") String productName,
            @Param("color") List<Color> color,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("size") List<Size> size,
            Pageable pageable);


    Product findByProductTitle(String productTitle);

    Optional<Product> findById(Integer productId);

    List<Product> findAllByCreatedBy(String email);

    Page<Product> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);

    Page<Product> findByPriceGreaterThanEqual(Double minPrice, Pageable pageable);

    Page<Product> findByPriceLessThanEqual(Double maxPrice, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.subCategory.subId = :subId")
    List<Product> findBySubcategoryId(@Param("subId") Integer subId);
}
