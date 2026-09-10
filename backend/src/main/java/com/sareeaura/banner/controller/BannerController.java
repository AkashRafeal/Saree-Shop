package com.sareeaura.banner.controller;

import com.sareeaura.banner.entity.Banner;
import com.sareeaura.banner.service.BannerService;
import com.sareeaura.common.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
@Tag(name = "Banners", description = "Endpoints for promotional hero banners")
public class BannerController {

    private final BannerService bannerService;

    @GetMapping
    @Operation(summary = "Get active homepage hero banners")
    public ResponseEntity<ApiResponse<List<Banner>>> getBanners() {
        return ResponseEntity.ok(ApiResponse.success("Banners retrieved", bannerService.getActiveBanners()));
    }
}
