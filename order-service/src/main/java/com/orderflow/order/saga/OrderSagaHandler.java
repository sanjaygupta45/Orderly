package com.orderflow.order.saga;

import com.orderflow.order.model.Order;
import com.orderflow.order.model.OrderStatus;
import com.orderflow.order.repository.OrderRepository;
import com.orderflow.shared.common.correlation.CorrelationId;
import com.orderflow.shared.common.idempotency.IdempotencyService;
import com.orderflow.shared.common.outbox.OutboxEventPublisher;
import com.orderflow.shared.events.InventoryFailedEvent;
import com.orderflow.shared.events.OrderCancelledEvent;
import com.orderflow.shared.events.OrderConfirmedEvent;
import com.orderflow.shared.events.PaymentCompletedEvent;
import com.orderflow.shared.events.PaymentFailedEvent;
import com.orderflow.shared.events.RoutingKeys;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Reacts to saga events and advances the order's lifecycle. Every handler is
// idempotent (skips duplicates) and transactional, so the state change and the
// next event are committed together through the outbox.
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderSagaHandler {

    private final OrderRepository orderRepository;
    private final IdempotencyService idempotency;
    private final OutboxEventPublisher outbox;
    private final MeterRegistry meterRegistry;

    // Payment succeeded -> confirm the order.
    @Transactional
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        MDC.put(CorrelationId.MDC_KEY, event.getCorrelationId());
        try {
            if (!idempotency.isNew(event.getEventId(), "PaymentCompleted")) {
                return;
            }
            Order order = load(event.getOrderId());
            if (order.getStatus() == OrderStatus.PENDING_PAYMENT) {
                order.setStatus(OrderStatus.CONFIRMED);
                outbox.save(RoutingKeys.ORDER_CONFIRMED, OrderConfirmedEvent.builder()
                        .orderId(order.getOrderId())
                        .userId(order.getUserId())
                        .correlationId(event.getCorrelationId())
                        .build());
                meterRegistry.counter("orderflow.orders", "status", "confirmed").increment();
                log.info("Order {} CONFIRMED", order.getOrderId());
            }
        } finally {
            MDC.remove(CorrelationId.MDC_KEY);
        }
    }

    // Payment failed -> cancel the order (inventory releases the stock separately).
    @Transactional
    public void onPaymentFailed(PaymentFailedEvent event) {
        MDC.put(CorrelationId.MDC_KEY, event.getCorrelationId());
        try {
            if (!idempotency.isNew(event.getEventId(), "PaymentFailed")) {
                return;
            }
            Order order = load(event.getOrderId());
            if (!order.getStatus().isTerminal()) {
                order.setStatus(OrderStatus.CANCELLED);
                order.setFailureReason(event.getReason());
                outbox.save(RoutingKeys.ORDER_CANCELLED, OrderCancelledEvent.builder()
                        .orderId(order.getOrderId())
                        .userId(order.getUserId())
                        .reason(event.getReason())
                        .correlationId(event.getCorrelationId())
                        .build());
                meterRegistry.counter("orderflow.orders", "status", "failed").increment();
                log.info("Order {} CANCELLED ({})", order.getOrderId(), event.getReason());
            }
        } finally {
            MDC.remove(CorrelationId.MDC_KEY);
        }
    }

    // Stock could not be reserved -> the order fails outright.
    @Transactional
    public void onInventoryFailed(InventoryFailedEvent event) {
        MDC.put(CorrelationId.MDC_KEY, event.getCorrelationId());
        try {
            if (!idempotency.isNew(event.getEventId(), "InventoryFailed")) {
                return;
            }
            Order order = load(event.getOrderId());
            if (!order.getStatus().isTerminal()) {
                order.setStatus(OrderStatus.FAILED);
                order.setFailureReason(event.getReason());
                // still emit order.cancelled so the customer gets a failure notification
                outbox.save(RoutingKeys.ORDER_CANCELLED, OrderCancelledEvent.builder()
                        .orderId(order.getOrderId())
                        .userId(order.getUserId())
                        .reason(event.getReason())
                        .correlationId(event.getCorrelationId())
                        .build());
                meterRegistry.counter("orderflow.orders", "status", "failed").increment();
                log.info("Order {} FAILED ({})", order.getOrderId(), event.getReason());
            }
        } finally {
            MDC.remove(CorrelationId.MDC_KEY);
        }
    }

    private Order load(String orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found for saga event: " + orderId));
    }
}
