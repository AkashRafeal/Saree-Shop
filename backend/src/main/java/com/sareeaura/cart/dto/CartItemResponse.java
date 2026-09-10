package com.sareeaura.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private String imageUrl;
    private BigDecimal mrp;
    private BigDecimal sellingPrice;
    private int quantity;
    private int stock;
    private BigDecimal subtotal;
}
