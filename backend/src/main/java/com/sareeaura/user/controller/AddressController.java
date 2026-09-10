package com.sareeaura.user.controller;

import com.sareeaura.common.api.ApiResponse;
import com.sareeaura.user.dto.AddressRequest;
import com.sareeaura.user.dto.AddressResponse;
import com.sareeaura.user.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "Addresses", description = "Endpoints for customer shipping addresses")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    @Operation(summary = "Get current customer's saved addresses")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses() {
        return ResponseEntity.ok(ApiResponse.success("Addresses retrieved", addressService.getUserAddresses()));
    }

    @PostMapping
    @Operation(summary = "Add a new shipping address")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(@Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Address added", addressService.addAddress(request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a shipping address")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted", null));
    }
}
