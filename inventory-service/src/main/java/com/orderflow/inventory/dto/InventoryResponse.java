package com.orderflow.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

// Inventory view for the admin API. Plain bean (not a record) so it serializes
// cleanly to/from the Redis cache.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse implements Serializable {
    private String skuCode;
    private Integer quantity;
    private Integer reservedQuantity;
    private Integer availableQuantity;
}
