package com.orderly.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {
    private boolean inStock;
    private String skuCode;
    private Integer quantity;
    private Integer availableQuantity;
}

