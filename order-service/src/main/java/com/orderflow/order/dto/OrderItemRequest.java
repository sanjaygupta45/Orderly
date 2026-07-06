package com.orderflow.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

// One requested line. unitPrice is supplied by the client in this demo; in a real
// system the catalog service would be the source of truth for price.
public record OrderItemRequest(
        @NotBlank String skuCode,
        @NotNull @Positive Integer quantity,
        @NotNull @Positive BigDecimal unitPrice) {
}
