package com.sareeaura.order.dto;

import com.sareeaura.order.entity.OrderStatus;
import com.sareeaura.user.dto.AddressResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal shippingCharge;
    private BigDecimal totalAmount;
    private String couponCode;
    private String paymentMethod;
    private String paymentStatus;
    private String trackingNumber;
    private String courierName;
    private AddressResponse shippingAddress;
    private List<OrderItemResponse> items;
    private int totalItems;
    private LocalDateTime createdAt;
}
