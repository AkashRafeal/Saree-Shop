package com.sareeaura.wishlist.controller;

import com.sareeaura.common.api.ApiResponse;
import com.sareeaura.wishlist.dto.WishlistResponse;
import com.sareeaura.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "Endpoints for customer wishlist items")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    @Operation(summary = "Get current customer's wishlist")
    public ResponseEntity<ApiResponse<WishlistResponse>> getWishlist() {
        return ResponseEntity.ok(ApiResponse.success("Wishlist retrieved", wishlistService.getWishlist()));
    }

    @PostMapping("/{productId}")
    @Operation(summary = "Toggle product in wishlist (add or remove)")
    public ResponseEntity<ApiResponse<WishlistResponse>> toggleWishlist(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success("Wishlist updated", wishlistService.toggleWishlist(productId)));
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Remove product from wishlist")
    public ResponseEntity<ApiResponse<WishlistResponse>> removeFromWishlist(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success("Product removed from wishlist", wishlistService.removeFromWishlist(productId)));
    }

    @PostMapping("/{productId}/move-to-cart")
    @Operation(summary = "Move product from wishlist to shopping cart")
    public ResponseEntity<ApiResponse<Void>> moveToCart(@PathVariable Long productId) {
        wishlistService.moveToCart(productId);
        return ResponseEntity.ok(ApiResponse.success("Product moved to cart", null));
    }
}
