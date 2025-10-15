package com.orderly.inventory_service.dto;

public record InventoryResponse(String skuCode, boolean isInStock) {
}