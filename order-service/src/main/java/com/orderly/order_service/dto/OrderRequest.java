package com.orderly.order_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderRequest(
            @NotBlank(message = "SKU code is required") String skuCode,

            @NotNull(message = "Price is required") @Positive(message = "Price must be positive") BigDecimal price,

            @NotNull(message = "Quantity is required") @Positive(message = "Quantity must be positive") Integer quantity) {
}
