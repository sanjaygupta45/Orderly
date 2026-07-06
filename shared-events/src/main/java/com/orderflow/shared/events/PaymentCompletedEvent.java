package com.orderflow.shared.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

// Published by payment-service on a successful charge.
// Order-service reacts by confirming the order.
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class PaymentCompletedEvent extends BaseEvent {
    private Long userId;
    private String paymentId;
    private BigDecimal amount;
}
