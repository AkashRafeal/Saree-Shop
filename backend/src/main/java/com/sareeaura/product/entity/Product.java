package com.sareeaura.product.entity;

import com.sareeaura.category.entity.Category;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
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

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(length = 100)
    private String fabric;

    @Column(length = 50)
    private String color;

    @Column(length = 100)
    private String pattern;

    @Column(length = 100)
    private String occasion;

    @Column(name = "saree_length", length = 50)
    @Builder.Default
    private String sareeLength = "5.5 Meters";

    @Column(name = "blouse_details", length = 100)
    @Builder.Default
    private String blouseDetails = "Includes 0.8 Meter Unstitched Blouse Piece";

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal mrp;

    @Column(name = "selling_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "discount_percentage")
    @Builder.Default
    private int discountPercentage = 0;

    @Column(nullable = false)
    @Builder.Default
    private int stock = 0;

    @Column(name = "low_stock_threshold")
    @Builder.Default
    private int lowStockThreshold = 5;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @Builder.Default
    @Column(name = "is_featured")
    private boolean isFeatured = false;

    @Builder.Default
    @Column(name = "is_best_seller")
    private boolean isBestSeller = false;

    @Builder.Default
    @Column(name = "is_new_arrival")
    private boolean isNewArrival = true;

    @Column(precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal rating = BigDecimal.valueOf(4.8);

    @Column(name = "review_count")
    @Builder.Default
    private int reviewCount = 0;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public String getPrimaryImageUrl() {
        if (images == null || images.isEmpty()) {
            return "https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=800&q=80";
        }
        for (ProductImage img : images) {
            if (img.isPrimary()) {
                String u = img.getImageUrl();
                if (u != null && u.contains("photo-1610030469830")) {
                    return "https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=800&q=80";
                }
                return u;
            }
        }
        String first = images.get(0).getImageUrl();
        if (first != null && first.contains("photo-1610030469830")) {
            return "https://images.unsplash.com/photo-1617627143750-d86bc21e42bb?auto=format&fit=crop&w=800&q=80";
        }
        return first;
    }
}
