package com.sareeshop.repository;

import com.sareeshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlug(String slug);

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    boolean existsBySlug(String slug);

    List<Product> findByFeaturedTrueAndActiveTrue(Pageable pageable);

    List<Product> findByTrendingTrueAndActiveTrue(Pageable pageable);

    List<Product> findByIsNewArrivalTrueAndActiveTrue(Pageable pageable);

    Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);

    @Query("SELECT DISTINCT p.fabric FROM Product p WHERE p.fabric IS NOT NULL AND p.active = true ORDER BY p.fabric ASC")
    List<String> findDistinctFabrics();

    @Query("SELECT DISTINCT p.color FROM Product p WHERE p.color IS NOT NULL AND p.active = true ORDER BY p.color ASC")
    List<String> findDistinctColors();

    @Query("SELECT DISTINCT p.occasion FROM Product p WHERE p.occasion IS NOT NULL AND p.active = true ORDER BY p.occasion ASC")
    List<String> findDistinctOccasions();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.active = true")
    long countActiveProducts();
}
