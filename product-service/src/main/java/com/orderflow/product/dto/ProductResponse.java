package com.orderflow.product.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String skuCode,
        String name,
        String description,
        String category,
        BigDecimal price) {
}
