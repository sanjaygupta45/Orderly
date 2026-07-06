package com.orderflow.shared.events;

// Routing keys used on the order.exchange topic exchange.
// Consumers bind their queue to the keys they care about.
public final class RoutingKeys {

    private RoutingKeys() {
    }

    public static final String ORDER_CREATED = "order.created";
    public static final String INVENTORY_RESERVED = "inventory.reserved";
    public static final String INVENTORY_FAILED = "inventory.failed";
    public static final String PAYMENT_COMPLETED = "payment.completed";
    public static final String PAYMENT_FAILED = "payment.failed";
    public static final String INVENTORY_RELEASED = "inventory.released";
    public static final String ORDER_CONFIRMED = "order.confirmed";
    public static final String ORDER_CANCELLED = "order.cancelled";
    public static final String NOTIFICATION_CREATED = "notification.created";
}
