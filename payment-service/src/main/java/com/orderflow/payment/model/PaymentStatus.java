package com.orderflow.payment.model;

// Outcome of a payment attempt. TIMEOUT is treated like a failure by the saga
// (the charge never confirmed), so it also triggers compensation.
public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    TIMEOUT
}
