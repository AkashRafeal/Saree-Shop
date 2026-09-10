package com.sareeaura.product.controller;

import com.sareeaura.common.api.ApiResponse;
import com.sareeaura.product.dto.ProductResponse;
import com.sareeaura.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Endpoints for browsing, searching, and filtering luxury Sarees")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get paginated products with multi-attribute filters")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String fabric,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String occasion,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean isNewArrival,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Page<ProductResponse> products = productService.getProductsWithFilters(
                search, categoryId, fabric, color, occasion, minPrice, maxPrice, isNewArrival, sort, page, size
        );
        return ResponseEntity.ok(ApiResponse.success("Products retrieved", products));
    }

    @GetMapping("/new-arrivals")
    @Operation(summary = "Get newest saree arrivals")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getNewArrivals() {
        return ResponseEntity.ok(ApiResponse.success("New arrivals", productService.getNewArrivals()));
    }

    @GetMapping("/best-sellers")
    @Operation(summary = "Get best selling sarees")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getBestSellers() {
        return ResponseEntity.ok(ApiResponse.success("Best sellers", productService.getBestSellers()));
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured sarees")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getFeaturedProducts() {
        return ResponseEntity.ok(ApiResponse.success("Featured products", productService.getFeaturedProducts()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed saree product by ID")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Product details", productService.getProductById(id)));
    }
}
