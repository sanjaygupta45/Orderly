package com.orderflow.shared.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

// Published by inventory-service once stock is successfully reserved.
// Payment-service reacts to charge the customer. Carries the amount to charge.
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class InventoryReservedEvent extends BaseEvent {
    private Long userId;
    private BigDecimal amount;
}
