package com.orderflow.shared.events;

// Names of the RabbitMQ exchanges. Kept in one place so producers and
// consumers can never drift apart.
public final class Exchanges {

    private Exchanges() {
    }

    // Main topic exchange. All saga events are published here and routed by key.
    public static final String ORDER_EXCHANGE = "order.exchange";

    // Dead-letter exchange. Messages that exhaust retries land here.
    public static final String ORDER_DLX = "order.dlx";
}
