package com.sareeaura.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long productId;
    private String customerName;
    private int rating;
    private String title;
    private String comment;
    private boolean verifiedPurchase;
    private boolean approved;
    private LocalDateTime createdAt;
    private String productTitle;
    private String productImage;
}
