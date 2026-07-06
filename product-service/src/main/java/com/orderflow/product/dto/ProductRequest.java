package com.orderflow.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank String skuCode,
        @NotBlank String name,
        String description,
        String category,
        @NotNull @Positive BigDecimal price) {
}
