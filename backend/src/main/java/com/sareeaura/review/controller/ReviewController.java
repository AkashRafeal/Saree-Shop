package com.sareeaura.review.controller;

import com.sareeaura.common.api.ApiResponse;
import com.sareeaura.review.dto.ReviewRequest;
import com.sareeaura.review.dto.ReviewResponse;
import com.sareeaura.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Endpoints for customer product reviews and ratings")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    @Operation(summary = "Get all approved customer reviews across all sarees")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getAllReviews() {
        return ResponseEntity.ok(ApiResponse.success("All reviews retrieved", reviewService.getAllReviews()));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get verified reviews for a specific saree product")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success("Reviews retrieved", reviewService.getProductReviews(productId)));
    }

    @PostMapping("/product/{productId}")
    @Operation(summary = "Submit a customer product review")
    public ResponseEntity<ApiResponse<ReviewResponse>> addReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Review submitted", reviewService.addReview(productId, request)));
    }
}
