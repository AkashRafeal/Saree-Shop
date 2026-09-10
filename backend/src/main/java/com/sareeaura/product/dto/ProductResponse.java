package com.sareeaura.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String sku;
    private String description;
    private Long categoryId;
    private String categoryName;
    private String categorySlug;
    private String fabric;
    private String color;
    private String pattern;
    private String occasion;
    private String sareeLength;
    private String blouseDetails;
    private BigDecimal mrp;
    private BigDecimal sellingPrice;
    private int discountPercentage;
    private int stock;
    private boolean inStock;
    private boolean isFeatured;
    private boolean isBestSeller;
    @com.fasterxml.jackson.annotation.JsonProperty("isNewArrival")
    private boolean isNewArrival;
    private BigDecimal rating;
    private int reviewCount;
    private String primaryImageUrl;
    private List<String> images;
}
