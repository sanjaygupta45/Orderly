package com.orderflow.order.saga;

import com.orderflow.order.model.Order;
import com.orderflow.order.model.OrderStatus;
import com.orderflow.order.repository.OrderRepository;
import com.orderflow.shared.common.idempotency.IdempotencyService;
import com.orderflow.shared.common.outbox.OutboxEventPublisher;
import com.orderflow.shared.events.InventoryFailedEvent;
import com.orderflow.shared.events.OrderCancelledEvent;
import com.orderflow.shared.events.OrderConfirmedEvent;
import com.orderflow.shared.events.PaymentCompletedEvent;
import com.orderflow.shared.events.PaymentFailedEvent;
import com.orderflow.shared.events.RoutingKeys;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderSagaHandlerTest {

    @Mock
    OrderRepository orderRepository;
    @Mock
    IdempotencyService idempotency;
    @Mock
    OutboxEventPublisher outbox;

    MeterRegistry meterRegistry = new SimpleMeterRegistry();
    OrderSagaHandler handler;

    @BeforeEach
    void setUp() {
        handler = new OrderSagaHandler(orderRepository, idempotency, outbox, meterRegistry);
    }

    private Order pendingOrder(String id) {
        Order order = new Order();
        order.setOrderId(id);
        order.setUserId(1L);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setTotalAmount(new BigDecimal("200"));
        return order;
    }

    @Test
    void paymentCompleted_confirmsOrder_andEmitsConfirmed() {
        Order order = pendingOrder("o1");
        when(idempotency.isNew(anyString(), anyString())).thenReturn(true);
        when(orderRepository.findByOrderId("o1")).thenReturn(Optional.of(order));

        handler.onPaymentCompleted(PaymentCompletedEvent.builder()
                .orderId("o1").userId(1L).paymentId("p1").amount(new BigDecimal("200")).build());

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        verify(outbox).save(eq(RoutingKeys.ORDER_CONFIRMED), any(OrderConfirmedEvent.class));
    }

    @Test
    void duplicateEvent_isIgnored() {
        when(idempotency.isNew(anyString(), anyString())).thenReturn(false);

        handler.onPaymentCompleted(PaymentCompletedEvent.builder().orderId("o1").build());

        verifyNoInteractions(outbox);
        verify(orderRepository, never()).findByOrderId(anyString());
    }

    @Test
    void paymentFailed_cancelsOrder_andEmitsCancelled() {
        Order order = pendingOrder("o2");
        when(idempotency.isNew(anyString(), anyString())).thenReturn(true);
        when(orderRepository.findByOrderId("o2")).thenReturn(Optional.of(order));

        handler.onPaymentFailed(PaymentFailedEvent.builder().orderId("o2").reason("declined").build());

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals("declined", order.getFailureReason());
        verify(outbox).save(eq(RoutingKeys.ORDER_CANCELLED), any(OrderCancelledEvent.class));
    }

    @Test
    void inventoryFailed_failsOrder_andEmitsCancelled() {
        Order order = pendingOrder("o3");
        when(idempotency.isNew(anyString(), anyString())).thenReturn(true);
        when(orderRepository.findByOrderId("o3")).thenReturn(Optional.of(order));

        handler.onInventoryFailed(InventoryFailedEvent.builder().orderId("o3").reason("out of stock").build());

        assertEquals(OrderStatus.FAILED, order.getStatus());
        verify(outbox).save(eq(RoutingKeys.ORDER_CANCELLED), any(OrderCancelledEvent.class));
    }

    @Test
    void terminalOrder_isNotChangedAgain() {
        Order order = pendingOrder("o4");
        order.setStatus(OrderStatus.CONFIRMED); // already terminal
        when(idempotency.isNew(anyString(), anyString())).thenReturn(true);
        when(orderRepository.findByOrderId("o4")).thenReturn(Optional.of(order));

        handler.onPaymentFailed(PaymentFailedEvent.builder().orderId("o4").reason("late").build());

        assertEquals(OrderStatus.CONFIRMED, order.getStatus()); // unchanged
        verifyNoInteractions(outbox);
    }
}
