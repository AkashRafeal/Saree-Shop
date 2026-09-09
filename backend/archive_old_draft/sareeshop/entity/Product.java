package com.sareeshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_slug", columnList = "slug", unique = true),
        @Index(name = "idx_product_sku", columnList = "sku", unique = true),
        @Index(name = "idx_product_fabric", columnList = "fabric"),
        @Index(name = "idx_product_color", columnList = "color"),
        @Index(name = "idx_product_occasion", columnList = "occasion"),
        @Index(name = "idx_product_price", columnList = "price")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, unique = true, length = 220)
    private String slug;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal mrp;

    @Builder.Default
    @Column(name = "discount_percentage")
    private Integer discountPercentage = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 80)
    private String fabric;

    @Column(nullable = false, length = 80)
    private String color;

    @Column(length = 80)
    private String occasion;

    @Column(length = 100)
    private String pattern;

    @Builder.Default
    @Column(name = "saree_length", length = 50)
    private String sareeLength = "5.5 Meters";

    @Builder.Default
    @Column(name = "blouse_included")
    private Boolean blouseIncluded = true;

    @Builder.Default
    @Column(name = "blouse_length", length = 50)
    private String blouseLength = "0.8 Meters";

    @Column(name = "blouse_type", length = 150)
    private String blouseType;

    @Column(name = "border_type", length = 100)
    private String borderType;

    @Column(name = "zari_type", length = 100)
    private String zariType;

    @Builder.Default
    @Column(name = "care_instructions", length = 255)
    private String careInstructions = "Dry Clean Only";

    @Builder.Default
    @Column(name = "weight_grams")
    private Integer weightGrams = 750;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Builder.Default
    @Column(name = "is_featured")
    private Boolean featured = false;

    @Builder.Default
    @Column(name = "is_trending")
    private Boolean trending = false;

    @Builder.Default
    @Column(name = "is_new_arrival")
    private Boolean isNewArrival = true;

    @Builder.Default
    @Column(name = "avg_rating")
    private Double avgRating = 0.0;

    @Builder.Default
    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<ProductImage> images = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variants = new ArrayList<>();

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Inventory inventory;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public String getPrimaryImageUrl() {
        if (images != null && !images.isEmpty()) {
            return images.stream()
                    .filter(ProductImage::getIsPrimary)
                    .findFirst()
                    .map(ProductImage::getImageUrl)
                    .orElse(images.get(0).getImageUrl());
        }
        return "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80";
    }
}
