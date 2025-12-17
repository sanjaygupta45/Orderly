package com.orderly.order_service.dto;

import java.math.BigDecimal;

public record OrderResponse(String orderNumber, String skuCode, BigDecimal price, Integer quantity) {
}
