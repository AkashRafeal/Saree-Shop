package com.sareeaura.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardStats {
    private BigDecimal totalRevenue;
    private long totalOrders;
    private long totalCustomers;
    private long totalProducts;
    private long pendingOrders;
    private long deliveredOrders;
    private long lowStockProducts;
    private List<Map<String, Object>> monthlySales;
    private List<Map<String, Object>> categoryDistribution;
}
