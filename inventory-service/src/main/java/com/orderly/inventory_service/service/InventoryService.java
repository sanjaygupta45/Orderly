package com.orderly.inventory_service.service;

import com.orderly.inventory_service.dto.InventoryResponse;

public interface InventoryService {

    boolean isInStock(String skuCode, Integer quantity);

    InventoryResponse addStock(String skuCode, Integer quantity);

    InventoryResponse reduceStock(String skuCode, Integer quantity);

    InventoryResponse getInventoryBySku(String skuCode);
}