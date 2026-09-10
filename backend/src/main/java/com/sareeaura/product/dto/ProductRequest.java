package com.sareeaura.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private String fabric;
    private String color;
    private String pattern;
    private String occasion;
    private String sareeLength;
    private String blouseDetails;

    @NotNull(message = "MRP is required")
    @Positive(message = "MRP must be greater than zero")
    private BigDecimal mrp;

    @NotNull(message = "Selling price is required")
    @Positive(message = "Selling price must be greater than zero")
    private BigDecimal sellingPrice;

    private int stock = 10;
    private boolean isFeatured = false;
    private boolean isBestSeller = false;
    private boolean isNewArrival = true;
    private List<String> imageUrls;
}
