package com.sareeaura.common.controller;

import com.sareeaura.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health & System", description = "Endpoints for checking system health and environment status")
public class HealthController {

    @GetMapping
    @Operation(summary = "Check backend and database health status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> healthData = Map.of(
                "application", "SareeAura Modular Monolith Backend",
                "version", "1.0.0",
                "architecture", "Modular Monolith",
                "phase", "PHASE_1_SETUP",
                "status", "UP",
                "database", "MySQL 8.0 (sareeaura_db)",
                "timestamp", LocalDateTime.now()
        );
        return ResponseEntity.ok(ApiResponse.success("SareeAura system is healthy and operational", healthData));
    }
}
