package com.orderflow.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(String skuCode, Integer quantity, BigDecimal unitPrice) {
}
