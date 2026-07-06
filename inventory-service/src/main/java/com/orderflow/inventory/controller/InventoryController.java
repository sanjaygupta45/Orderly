package com.orderflow.inventory.controller;

import com.orderflow.inventory.dto.AddStockRequest;
import com.orderflow.inventory.dto.InventoryResponse;
import com.orderflow.inventory.service.InventoryService;
import com.orderflow.shared.common.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Admin/query REST. Stock is reserved/released via events, not here - these
// endpoints only seed stock and expose availability.
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    // Seed / top up stock for a SKU (admin operation).
    @PostMapping
    public ResponseEntity<ApiResponse<InventoryResponse>> addStock(@Valid @RequestBody AddStockRequest request) {
        InventoryResponse response = inventoryService.addStock(request.skuCode(), request.quantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Stock added", response));
    }

    // Current availability for a SKU.
    @GetMapping("/{skuCode}")
    public ResponseEntity<ApiResponse<InventoryResponse>> getBySku(@PathVariable String skuCode) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getBySku(skuCode)));
    }
}
