package com.sareeaura.cart.controller;

import com.sareeaura.cart.dto.AddToCartRequest;
import com.sareeaura.cart.dto.CartResponse;
import com.sareeaura.cart.service.CartService;
import com.sareeaura.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Endpoints for managing customer shopping cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get current customer's shopping cart")
    public ResponseEntity<ApiResponse<CartResponse>> getCart() {
        return ResponseEntity.ok(ApiResponse.success("Cart retrieved", cartService.getCart()));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(@Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Product added to cart", cartService.addToCart(request)));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update cart item quantity")
    public ResponseEntity<ApiResponse<CartResponse>> updateQuantity(
            @PathVariable Long itemId,
            @RequestParam int quantity
    ) {
        return ResponseEntity.ok(ApiResponse.success("Cart item quantity updated", cartService.updateQuantity(itemId, quantity)));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", cartService.removeFromCart(itemId)));
    }

    @DeleteMapping
    @Operation(summary = "Clear all items from cart")
    public ResponseEntity<ApiResponse<CartResponse>> clearCart() {
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", cartService.clearCart()));
    }
}
