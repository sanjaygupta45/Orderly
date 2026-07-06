package com.orderflow.shared.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

// Published by payment-service when a charge fails or times out.
// Triggers compensation: inventory-service releases the reserved stock and
// order-service cancels the order.
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class PaymentFailedEvent extends BaseEvent {
    private Long userId;
    private String reason;
}
