package com.sareeaura.coupon.controller;

import com.sareeaura.common.api.ApiResponse;
import com.sareeaura.coupon.dto.CouponResponse;
import com.sareeaura.coupon.dto.ValidateCouponRequest;
import com.sareeaura.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupons", description = "Endpoints for coupon validation and discount application")
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/validate")
    @Operation(summary = "Validate coupon code and compute backend discount")
    public ResponseEntity<ApiResponse<CouponResponse>> validateCoupon(@Valid @RequestBody ValidateCouponRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Coupon validated", couponService.validateCoupon(request)));
    }
}
