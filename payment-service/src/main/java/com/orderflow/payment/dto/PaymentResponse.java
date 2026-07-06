package com.orderflow.payment.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        String paymentId,
        String orderId,
        BigDecimal amount,
        String status,
        String failureReason,
        Instant createdAt) {
}
