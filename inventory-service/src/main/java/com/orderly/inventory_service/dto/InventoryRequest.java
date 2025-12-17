package com.orderly.inventory_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequest {

      @NotBlank(message = "SKU Code cannot be empty")
      private String skuCode;

      @Min(value = 1, message = "Quantity must be greater than 0")
      private Integer quantity;
}
