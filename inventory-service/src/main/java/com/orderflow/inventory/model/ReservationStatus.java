package com.orderflow.inventory.model;

// A reservation is RESERVED when stock is held for an order, and RELEASED once
// it's given back (compensation after a payment failure).
public enum ReservationStatus {
    RESERVED,
    RELEASED
}
