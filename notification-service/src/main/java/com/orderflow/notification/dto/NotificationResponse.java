package com.orderflow.notification.dto;

import java.time.Instant;

public record NotificationResponse(
        String orderId,
        Long userId,
        String channel,
        String type,
        String content,
        String status,
        Instant createdAt) {
}
