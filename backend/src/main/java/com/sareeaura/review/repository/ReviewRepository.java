package com.sareeaura.review.repository;

import com.sareeaura.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdAndApprovedTrueOrderByCreatedAtDesc(Long productId);
    List<Review> findByApprovedTrueOrderByCreatedAtDesc();
    boolean existsByProductIdAndUserId(Long productId, Long userId);
}
