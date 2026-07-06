package com.orderflow.inventory.service;

import com.orderflow.inventory.dto.InventoryResponse;

// Admin/query side of inventory (REST). The saga side lives in StockService.
public interface InventoryService {

    InventoryResponse addStock(String skuCode, int quantity);

    InventoryResponse getBySku(String skuCode);
}
