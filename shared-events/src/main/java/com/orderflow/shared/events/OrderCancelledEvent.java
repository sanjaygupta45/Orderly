package com.orderflow.shared.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

// Published by order-service when the order ends up CANCELLED/FAILED
// (out of stock, or payment failed). Notification-service reacts with a
// failure message. Terminal (unhappy) event.
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class OrderCancelledEvent extends BaseEvent {
    private Long userId;
    private String reason;
}
