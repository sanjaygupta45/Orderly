package com.orderflow.shared.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

// Envelope carried by every event in the platform.
// - eventId: unique per event, lets consumers dedupe (idempotency)
// - correlationId: same value across one order's whole flow, so logs/traces line up
// - timestamp/version: audit + safe payload evolution
// - orderId: the order this event is about (order-service's public id)
@Getter
@Setter
@NoArgsConstructor          // Jackson deserializes via no-arg constructor + setters
@SuperBuilder               // producers build events fluently
public abstract class BaseEvent implements Serializable {

    @lombok.Builder.Default
    private String eventId = UUID.randomUUID().toString();

    private String correlationId;

    @lombok.Builder.Default
    private Instant timestamp = Instant.now();

    @lombok.Builder.Default
    private int version = 1;

    private String orderId;
}
