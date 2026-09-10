package com.sareeaura.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {

    @NotNull(message = "Shipping address is required")
    private Long addressId;

    private String couponCode;

    private String paymentMethod = "RAZORPAY";
}
