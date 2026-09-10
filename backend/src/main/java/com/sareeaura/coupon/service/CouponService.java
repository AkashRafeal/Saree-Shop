package com.sareeaura.coupon.service;

import com.sareeaura.auth.service.AuthService;
import com.sareeaura.coupon.dto.CouponResponse;
import com.sareeaura.coupon.dto.ValidateCouponRequest;
import com.sareeaura.coupon.entity.Coupon;
import com.sareeaura.coupon.entity.DiscountType;
import com.sareeaura.coupon.repository.CouponRepository;
import com.sareeaura.coupon.repository.CouponUsageRepository;
import com.sareeaura.exception.BadRequestException;
import com.sareeaura.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import com.sareeaura.exception.ResourceNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final AuthService authService;

    public List<Coupon> getAllCoupons() {
        List<Coupon> list = couponRepository.findAll();
        if (list.isEmpty()) {
            Coupon c1 = Coupon.builder()
                    .code("WELCOME10")
                    .description("Welcome discount for new luxury patrons")
                    .discountType(DiscountType.PERCENTAGE)
                    .discountValue(BigDecimal.valueOf(10))
                    .minOrderAmount(BigDecimal.valueOf(1000))
                    .maxDiscountAmount(BigDecimal.valueOf(2500))
                    .usageLimit(1000)
                    .usageCount(14)
                    .perUserLimit(1)
                    .active(true)
                    .build();
            Coupon c2 = Coupon.builder()
                    .code("BRIDAL20")
                    .description("Exclusive 20% off for Bridal & Wedding heritage sarees")
                    .discountType(DiscountType.PERCENTAGE)
                    .discountValue(BigDecimal.valueOf(20))
                    .minOrderAmount(BigDecimal.valueOf(15000))
                    .maxDiscountAmount(BigDecimal.valueOf(8000))
                    .usageLimit(500)
                    .usageCount(6)
                    .perUserLimit(2)
                    .active(true)
                    .build();
            Coupon c3 = Coupon.builder()
                    .code("SILK500")
                    .description("Flat ₹500 discount on pure handloom silk collections")
                    .discountType(DiscountType.FIXED)
                    .discountValue(BigDecimal.valueOf(500))
                    .minOrderAmount(BigDecimal.valueOf(4999))
                    .usageLimit(2000)
                    .usageCount(38)
                    .perUserLimit(3)
                    .active(true)
                    .build();
            couponRepository.saveAll(List.of(c1, c2, c3));
            return couponRepository.findAll();
        }
        return list;
    }

    public Coupon createCoupon(Coupon coupon) {
        if (coupon.getCode() != null) {
            coupon.setCode(coupon.getCode().trim().toUpperCase());
        }
        return couponRepository.save(coupon);
    }

    public Coupon updateCoupon(Long id, Coupon request) {
        Coupon existing = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", id));
        if (request.getCode() != null) existing.setCode(request.getCode().trim().toUpperCase());
        if (request.getDescription() != null) existing.setDescription(request.getDescription());
        if (request.getDiscountType() != null) existing.setDiscountType(request.getDiscountType());
        if (request.getDiscountValue() != null) existing.setDiscountValue(request.getDiscountValue());
        if (request.getMinOrderAmount() != null) existing.setMinOrderAmount(request.getMinOrderAmount());
        if (request.getMaxDiscountAmount() != null) existing.setMaxDiscountAmount(request.getMaxDiscountAmount());
        if (request.getUsageLimit() != null) existing.setUsageLimit(request.getUsageLimit());
        existing.setActive(request.isActive());
        return couponRepository.save(existing);
    }

    public void deleteCoupon(Long id) {
        couponRepository.deleteById(id);
    }

    public CouponResponse validateCoupon(ValidateCouponRequest request) {
        Optional<Coupon> opt = couponRepository.findByCodeIgnoreCaseAndActiveTrue(request.getCode().trim());
        if (opt.isEmpty()) {
            throw new BadRequestException("Invalid or inactive coupon code: " + request.getCode());
        }

        Coupon coupon = opt.get();
        if (!coupon.isValid()) {
            throw new BadRequestException("This coupon has expired or reached maximum redemption limits");
        }

        if (request.getOrderAmount().compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new BadRequestException("Minimum order amount of ₹" + coupon.getMinOrderAmount() + " required to apply this coupon");
        }

        try {
            User user = authService.getCurrentUser();
            long usages = couponUsageRepository.countByCouponIdAndUserId(coupon.getId(), user.getId());
            if (usages >= coupon.getPerUserLimit()) {
                throw new BadRequestException("You have already used this coupon maximum allowed times");
            }
        } catch (Exception e) {
            // If user is guest/checking before auth, skip user-level check
        }

        BigDecimal discount;
        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = request.getOrderAmount().multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (coupon.getMaxDiscountAmount() != null && discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                discount = coupon.getMaxDiscountAmount();
            }
        } else {
            discount = coupon.getDiscountValue();
            if (discount.compareTo(request.getOrderAmount()) > 0) {
                discount = request.getOrderAmount();
            }
        }

        return CouponResponse.builder()
                .code(coupon.getCode())
                .description(coupon.getDescription())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .calculatedDiscount(discount)
                .valid(true)
                .message("Coupon applied successfully!")
                .build();
    }
}
