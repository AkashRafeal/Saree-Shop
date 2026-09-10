package com.sareeaura.review.service;

import com.sareeaura.auth.service.AuthService;
import com.sareeaura.exception.ResourceNotFoundException;
import com.sareeaura.product.entity.Product;
import com.sareeaura.product.repository.ProductRepository;
import com.sareeaura.review.dto.ReviewRequest;
import com.sareeaura.review.dto.ReviewResponse;
import com.sareeaura.review.entity.Review;
import com.sareeaura.review.repository.ReviewRepository;
import com.sareeaura.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final AuthService authService;

    public List<ReviewResponse> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdAndApprovedTrueOrderByCreatedAtDesc(productId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponse addReview(Long productId, ReviewRequest request) {
        User user = authService.getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(request.getRating())
                .title(request.getTitle())
                .comment(request.getComment())
                .verifiedPurchase(true)
                .approved(true)
                .build();

        review = reviewRepository.save(review);

        // Update product average rating
        List<Review> allReviews = reviewRepository.findByProductIdAndApprovedTrueOrderByCreatedAtDesc(productId);
        double avg = allReviews.stream().mapToInt(Review::getRating).average().orElse(request.getRating());
        product.setRating(BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP));
        product.setReviewCount(allReviews.size());
        productRepository.save(product);

        return mapToResponse(review);
    }

    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findByApprovedTrueOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ReviewResponse> getAllAdminReviews() {
        return reviewRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponse toggleApproval(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
        review.setApproved(!review.isApproved());
        return mapToResponse(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    private ReviewResponse mapToResponse(Review r) {
        String firstName = r.getUser() != null && r.getUser().getFirstName() != null ? r.getUser().getFirstName() : "Patron";
        String lastName = r.getUser() != null && r.getUser().getLastName() != null ? r.getUser().getLastName() : "";
        String customerName = lastName.isEmpty() ? firstName : firstName + " " + lastName.substring(0, 1) + ".";

        String prodTitle = r.getProduct() != null ? r.getProduct().getName() : "";
        String prodImage = r.getProduct() != null ? r.getProduct().getPrimaryImageUrl() : null;

        return ReviewResponse.builder()
                .id(r.getId())
                .productId(r.getProduct() != null ? r.getProduct().getId() : null)
                .productTitle(prodTitle)
                .productImage(prodImage)
                .customerName(customerName)
                .rating(r.getRating())
                .title(r.getTitle())
                .comment(r.getComment())
                .verifiedPurchase(r.isVerifiedPurchase())
                .approved(r.isApproved())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
