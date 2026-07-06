package com.orderflow.shared.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

// Published by inventory-service when stock cannot be reserved (e.g. out of stock).
// Order-service reacts by marking the order FAILED. No compensation needed yet
// because nothing downstream has run.
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class InventoryFailedEvent extends BaseEvent {
    private String reason;
}
