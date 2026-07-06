package com.orderflow.shared.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

// Published by inventory-service after it compensates by releasing reserved stock
// (reaction to payment failure). Mostly for audit / order-service bookkeeping.
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class InventoryReleasedEvent extends BaseEvent {
    private String reason;
}
