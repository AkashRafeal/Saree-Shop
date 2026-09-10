package com.sareeaura.product.repository;

import com.sareeaura.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    List<Product> findTop8ByActiveTrueOrderByCreatedAtDesc();

    List<Product> findByActiveTrueAndIsNewArrivalTrueOrderByCreatedAtDesc();

    List<Product> findTop8ByActiveTrueAndIsBestSellerTrue();

    List<Product> findTop8ByActiveTrueAndIsFeaturedTrue();

    List<Product> findByCategoryIdAndActiveTrue(Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(:isNewArrival IS NULL OR p.isNewArrival = :isNewArrival) AND " +
           "(:query IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(p.fabric) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(p.color) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(p.occasion) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(p.sku) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:fabric IS NULL OR LOWER(p.fabric) = LOWER(:fabric)) AND " +
           "(:color IS NULL OR LOWER(p.color) = LOWER(:color)) AND " +
           "(:occasion IS NULL OR LOWER(p.occasion) = LOWER(:occasion)) AND " +
           "(:minPrice IS NULL OR p.sellingPrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.sellingPrice <= :maxPrice)")
    Page<Product> findWithFilters(
            @Param("query") String query,
            @Param("categoryId") Long categoryId,
            @Param("fabric") String fabric,
            @Param("color") String color,
            @Param("occasion") String occasion,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("isNewArrival") Boolean isNewArrival,
            Pageable pageable
    );
}
