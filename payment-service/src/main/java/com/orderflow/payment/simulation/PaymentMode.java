package com.orderflow.payment.simulation;

// How the fake gateway decides an outcome.
public enum PaymentMode {
    RANDOM,          // succeed with configured probability, otherwise fail/timeout
    ALWAYS_SUCCESS,  // handy for the happy-path demo
    ALWAYS_FAIL      // handy for demoing the compensation path
}
