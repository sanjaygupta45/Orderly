package com.orderly.inventory_service.repository;


import com.orderly.inventory_service.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Inventory Repository - Data access layer for inventory operations
 */
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    boolean existsBySkuCodeAndQuantityIsGreaterThanEqual(String skuCode, int quantity);
    Optional<Inventory> findBySkuCode(String skuCode);
}
