package com.orderflow.order.model;

// Lifecycle of an order. The saga moves it between these states.
public enum OrderStatus {
    PENDING_PAYMENT,   // created; waiting for inventory + payment
    CONFIRMED,         // payment succeeded - happy path complete
    FAILED,            // stock could not be reserved
    CANCELLED;         // payment failed - compensated

    public boolean isTerminal() {
        return this == CONFIRMED || this == FAILED || this == CANCELLED;
    }
}
