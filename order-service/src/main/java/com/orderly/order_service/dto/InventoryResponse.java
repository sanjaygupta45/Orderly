package com.orderly.order_service.dto;

public record InventoryResponse(
        boolean inStock,
        String skuCode,
        Integer quantity,
        Integer availableQuantity) {
}
