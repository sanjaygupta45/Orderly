package com.orderly.inventory_service.service;

import com.orderly.inventory_service.dto.InventoryResponse;
import com.orderly.inventory_service.model.Inventory;
import com.orderly.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Inventory Service - Handles stock validation and inventory operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    /**
     * Check if product is in stock for the given quantity
     *
     * @param skuCode Product SKU code
     * @param quantity Quantity required
     * @return true if in stock, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isInStock(String skuCode, Integer quantity) {
        log.debug("Checking stock availability for SKU: {}, Quantity: {}", skuCode, quantity);
        boolean inStock = inventoryRepository.existsBySkuCodeAndQuantityIsGreaterThanEqual(skuCode, quantity);
        log.debug("Stock check result for SKU {}: {}", skuCode, inStock);
        return inStock;
    }

    /**
     * Get inventory details for a specific SKU
     *
     * @param skuCode Product SKU code
     * @return Inventory details
     */
    @Transactional(readOnly = true)
    public InventoryResponse getInventoryBySku(String skuCode) {
        log.debug("Fetching inventory details for SKU: {}", skuCode);

        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> {
                    log.warn("Inventory not found for SKU: {}", skuCode);
                    return new IllegalArgumentException("Inventory not found for SKU: " + skuCode);
                });

        return InventoryResponse.builder()
                .id(inventory.getId())
                .skuCode(inventory.getSkuCode())
                .quantity(inventory.getQuantity())
                .inStock(inventory.getQuantity() > 0)
                .build();
    }
}