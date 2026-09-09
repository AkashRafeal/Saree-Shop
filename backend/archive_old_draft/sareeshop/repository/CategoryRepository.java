package com.sareeshop.repository;

import com.sareeshop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Category> findByParentIsNullAndActiveTrueOrderByDisplayOrderAsc();

    List<Category> findByActiveTrueOrderByDisplayOrderAsc();

    List<Category> findByParentIdAndActiveTrueOrderByDisplayOrderAsc(Long parentId);
}
