package com.orderflow.shared.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

// Published by order-service after an order is saved (status PENDING_PAYMENT).
// Inventory-service reacts to reserve stock. Starts the saga.
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class OrderCreatedEvent extends BaseEvent {
    private Long userId;
    private BigDecimal totalAmount;
    private List<OrderLineItem> items;
}
