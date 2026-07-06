package com.orderflow.inventory.service.impl;

import com.orderflow.inventory.dto.InventoryResponse;
import com.orderflow.inventory.model.Inventory;
import com.orderflow.inventory.repository.InventoryRepository;
import com.orderflow.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    // Adding stock changes availability, so drop the cached entry for this SKU.
    @Override
    @Transactional
    @CacheEvict(value = "inventory", key = "#skuCode")
    public InventoryResponse addStock(String skuCode, int quantity) {
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseGet(() -> new Inventory(skuCode, 0));
        inventory.setQuantity(inventory.getQuantity() + quantity);
        Inventory saved = inventoryRepository.save(inventory);
        log.info("Added {} to SKU {} (on-hand now {})", quantity, skuCode, saved.getQuantity());
        return toResponse(saved);
    }

    // Cached read: frequently hit by clients checking availability.
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "inventory", key = "#skuCode")
    public InventoryResponse getBySku(String skuCode) {
        return inventoryRepository.findBySkuCode(skuCode)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No inventory for SKU: " + skuCode));
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .skuCode(inventory.getSkuCode())
                .quantity(inventory.getQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .availableQuantity(inventory.availableQuantity())
                .build();
    }
}
