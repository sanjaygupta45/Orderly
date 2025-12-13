package com.orderly.inventory_service.controller;

import com.orderly.inventory_service.dto.InventoryResponse;
import com.orderly.inventory_service.exception.ApiError;
import com.orderly.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Inventory Controller - Handles inventory stock validation
 */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Check if a product is in stock.
     *
     * @param skuCode Product SKU code
     * @param quantity Quantity required
     * @return true if in stock, false otherwise
     */
    @GetMapping
    public ResponseEntity<Boolean> isInStock(@RequestParam String skuCode, @RequestParam Integer quantity) {
        try {
            log.info("Checking stock for SKU: {}, Quantity: {}", skuCode, quantity);
            boolean inStock = inventoryService.isInStock(skuCode, quantity);
            log.info("Stock check result for SKU {}: {}", skuCode, inStock);
            return new ResponseEntity<>(inStock, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error checking stock for SKU: {}", skuCode, e);
            throw new RuntimeException("Error checking inventory: " + e.getMessage(), e);
        }
    }

    /**
     * Get inventory details for a SKU.
     *
     * @param skuCode Product SKU code
     * @return Inventory details
     */
    @GetMapping("/details")
    public ResponseEntity<ApiError> getInventoryDetails(@RequestParam String skuCode) {
        try {
            log.info("Fetching inventory details for SKU: {}", skuCode);
            InventoryResponse inventory = inventoryService.getInventoryBySku(skuCode);

            ApiError response = ApiError.builder()
                    .success(true)
                    .message("Inventory details retrieved successfully")
                    .status(HttpStatus.OK.value())
                    .timestamp(LocalDateTime.now())
                    .details(inventory)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching inventory for SKU: {}", skuCode, e);
            ApiError response = ApiError.builder()
                    .success(false)
                    .message("Error fetching inventory: " + e.getMessage())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .timestamp(LocalDateTime.now())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

