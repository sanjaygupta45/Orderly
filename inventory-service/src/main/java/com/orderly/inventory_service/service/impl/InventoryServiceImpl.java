package com.orderly.inventory_service.service.impl;

import com.orderly.inventory_service.dto.InventoryResponse;
import com.orderly.inventory_service.model.Inventory;
import com.orderly.inventory_service.repository.InventoryRepository;
import com.orderly.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

      private final InventoryRepository inventoryRepository;

      @Override
      @Transactional(readOnly = true)
      public boolean isInStock(String skuCode, Integer quantity) {
            log.debug("Checking stock availability for SKU: {}, Quantity: {}", skuCode, quantity);
            boolean inStock = inventoryRepository.existsBySkuCodeAndQuantityIsGreaterThanEqual(skuCode, quantity);
            log.debug("Stock check result for SKU {}: {}", skuCode, inStock);
            return inStock;
      }

      @Override
      @Transactional
      public InventoryResponse addStock(String skuCode, Integer quantity) {
            log.info("Adding stock for SKU: {}, Quantity: {}", skuCode, quantity);

            Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                        .orElseGet(() -> {
                              log.info("Creating new inventory for SKU: {}", skuCode);
                              return new Inventory(null, skuCode, 0);
                        });

            inventory.setQuantity(inventory.getQuantity() + quantity);
            Inventory savedInventory = inventoryRepository.save(inventory);

            return mapToInventoryResponse(savedInventory);
      }

      @Override
      @Transactional
      public InventoryResponse reduceStock(String skuCode, Integer quantity) {
            log.info("Reducing stock for SKU: {}, Quantity: {}", skuCode, quantity);

            Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                        .orElseThrow(() -> new IllegalArgumentException("Inventory not found for SKU: " + skuCode));

            if (inventory.getQuantity() < quantity) {
                  log.warn("Insufficient stock for SKU: {}. Available: {}, Requested: {}",
                              skuCode, inventory.getQuantity(), quantity);
                  throw new IllegalArgumentException("Insufficient stock for SKU: " + skuCode);
            }

            inventory.setQuantity(inventory.getQuantity() - quantity);
            Inventory savedInventory = inventoryRepository.save(inventory);

            return mapToInventoryResponse(savedInventory);
      }

      @Override
      @Transactional(readOnly = true)
      public InventoryResponse getInventoryBySku(String skuCode) {
            log.debug("Fetching inventory details for SKU: {}", skuCode);

            Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                        .orElseThrow(() -> {
                              log.warn("Inventory not found for SKU: {}", skuCode);
                              return new IllegalArgumentException("Inventory not found for SKU: " + skuCode);
                        });

            return mapToInventoryResponse(inventory);
      }

      private InventoryResponse mapToInventoryResponse(Inventory inventory) {
            return InventoryResponse.builder()
                        .id(inventory.getId())
                        .skuCode(inventory.getSkuCode())
                        .quantity(inventory.getQuantity())
                        .inStock(inventory.getQuantity() > 0)
                        .build();
      }
}
