package com.sareeaura.order.controller;

import com.sareeaura.common.api.ApiResponse;
import com.sareeaura.order.dto.CreateOrderRequest;
import com.sareeaura.order.dto.OrderResponse;
import com.sareeaura.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Endpoints for customer order creation, history, and tracking")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Place order from customer shopping cart")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Order placed successfully", orderService.createOrder(request)));
    }

    @GetMapping
    @Operation(summary = "Get list of customer orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders() {
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved", orderService.getCustomerOrders()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed order information and tracking timeline")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Order details", orderService.getOrderById(id)));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel customer order")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Order cancelled", orderService.cancelOrder(id)));
    }
}
