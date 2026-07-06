package com.orderflow.shared.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

// Published by order-service once payment succeeds and the order is CONFIRMED.
// Notification-service reacts with a success message. Terminal (happy) event.
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class OrderConfirmedEvent extends BaseEvent {
    private Long userId;
}
