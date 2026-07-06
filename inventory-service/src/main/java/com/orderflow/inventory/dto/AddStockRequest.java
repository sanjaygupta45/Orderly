package com.orderflow.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Admin request to add stock for a SKU (used to seed inventory).
public record AddStockRequest(
        @NotBlank String skuCode,
        @NotNull @Min(1) Integer quantity) {
}
