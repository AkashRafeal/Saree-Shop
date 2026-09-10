package com.sareeaura.admin.controller;

import com.sareeaura.admin.dto.AdminDashboardStats;
import com.sareeaura.admin.dto.UpdateOrderStatusRequest;
import com.sareeaura.admin.service.AdminService;
import com.sareeaura.auth.dto.UserDto;
import com.sareeaura.category.dto.CategoryDto;
import com.sareeaura.category.service.CategoryService;
import com.sareeaura.common.api.ApiResponse;
import com.sareeaura.order.dto.OrderResponse;
import com.sareeaura.order.service.OrderService;
import com.sareeaura.product.dto.ProductRequest;
import com.sareeaura.product.dto.ProductResponse;
import com.sareeaura.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.sareeaura.banner.entity.Banner;
import com.sareeaura.banner.service.BannerService;
import com.sareeaura.coupon.entity.Coupon;
import com.sareeaura.coupon.service.CouponService;
import com.sareeaura.review.dto.ReviewResponse;
import com.sareeaura.review.service.ReviewService;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Operations", description = "Endpoints for administrators to manage products, orders, and review KPIs")
public class AdminController {

    private final AdminService adminService;
    private final ProductService productService;
    private final OrderService orderService;
    private final CategoryService categoryService;
    private final CouponService couponService;
    private final BannerService bannerService;
    private final ReviewService reviewService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard KPI metrics and sales analytics")
    public ResponseEntity<ApiResponse<AdminDashboardStats>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard metrics retrieved", adminService.getDashboardStats()));
    }

    @GetMapping("/customers")
    @Operation(summary = "Get registered customer list")
    public ResponseEntity<ApiResponse<List<UserDto>>> getCustomers() {
        return ResponseEntity.ok(ApiResponse.success("Customer list retrieved", adminService.getAllCustomers()));
    }

    @DeleteMapping("/customers/{id}")
    @Operation(summary = "Delete customer account by ID")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long id) {
        adminService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success("Customer removed successfully", null));
    }

    @GetMapping("/orders")
    @Operation(summary = "Get all customer orders for admin review")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved", orderService.getAllOrders(PageRequest.of(page, size))));
    }

    @PutMapping("/orders/{id}/status")
    @Operation(summary = "Update order delivery lifecycle status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Order status updated", orderService.updateOrderStatus(id, request.getStatus())));
    }

    @PostMapping("/products")
    @Operation(summary = "Create a new saree product")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Product created", productService.createProduct(request)));
    }

    @PutMapping("/products/{id}")
    @Operation(summary = "Update an existing saree product")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Product updated", productService.updateProduct(id, request)));
    }

    @DeleteMapping("/products/{id}")
    @Operation(summary = "Deactivate product")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deactivated", null));
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all categories for admin catalog control")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved", categoryService.getAllCategories()));
    }

    @PostMapping("/categories")
    @Operation(summary = "Create a new saree category / weave")
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(@RequestBody CategoryDto request) {
        if (request.getSlug() == null || request.getSlug().isBlank()) {
            request.setSlug(request.getName().toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", ""));
        }
        return ResponseEntity.ok(ApiResponse.success("Category created", categoryService.createCategory(request)));
    }

    @PutMapping("/categories/{id}")
    @Operation(summary = "Update an existing saree category")
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryDto request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Category updated", categoryService.updateCategory(id, request)));
    }

    @DeleteMapping("/categories/{id}")
    @Operation(summary = "Delete category")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category removed successfully", null));
    }

    // Coupons Management
    @GetMapping("/coupons")
    @Operation(summary = "Get all promotional coupons")
    public ResponseEntity<ApiResponse<List<Coupon>>> getCoupons() {
        return ResponseEntity.ok(ApiResponse.success("Coupons retrieved", couponService.getAllCoupons()));
    }

    @PostMapping("/coupons")
    @Operation(summary = "Create a new coupon")
    public ResponseEntity<ApiResponse<Coupon>> createCoupon(@RequestBody Coupon coupon) {
        return ResponseEntity.ok(ApiResponse.success("Coupon created", couponService.createCoupon(coupon)));
    }

    @PutMapping("/coupons/{id}")
    @Operation(summary = "Update an existing coupon")
    public ResponseEntity<ApiResponse<Coupon>> updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
        return ResponseEntity.ok(ApiResponse.success("Coupon updated", couponService.updateCoupon(id, coupon)));
    }

    @DeleteMapping("/coupons/{id}")
    @Operation(summary = "Delete coupon")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok(ApiResponse.success("Coupon removed", null));
    }

    // Banners Management
    @GetMapping("/banners")
    @Operation(summary = "Get all promotional banners")
    public ResponseEntity<ApiResponse<List<Banner>>> getBanners() {
        return ResponseEntity.ok(ApiResponse.success("Banners retrieved", bannerService.getAllBanners()));
    }

    @PostMapping("/banners")
    @Operation(summary = "Create a new banner")
    public ResponseEntity<ApiResponse<Banner>> createBanner(@RequestBody Banner banner) {
        return ResponseEntity.ok(ApiResponse.success("Banner created", bannerService.createBanner(banner)));
    }

    @PutMapping("/banners/{id}")
    @Operation(summary = "Update an existing banner")
    public ResponseEntity<ApiResponse<Banner>> updateBanner(@PathVariable Long id, @RequestBody Banner banner) {
        return ResponseEntity.ok(ApiResponse.success("Banner updated", bannerService.updateBanner(id, banner)));
    }

    @DeleteMapping("/banners/{id}")
    @Operation(summary = "Delete banner")
    public ResponseEntity<ApiResponse<Void>> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.ok(ApiResponse.success("Banner removed", null));
    }

    // Reviews Moderation
    @GetMapping("/reviews")
    @Operation(summary = "Get all customer reviews for moderation")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviews() {
        return ResponseEntity.ok(ApiResponse.success("Reviews retrieved", reviewService.getAllAdminReviews()));
    }

    @PutMapping("/reviews/{id}/status")
    @Operation(summary = "Toggle review approval status")
    public ResponseEntity<ApiResponse<ReviewResponse>> toggleReviewStatus(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Review status updated", reviewService.toggleApproval(id)));
    }

    @DeleteMapping("/reviews/{id}")
    @Operation(summary = "Delete review")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(ApiResponse.success("Review deleted", null));
    }
}
