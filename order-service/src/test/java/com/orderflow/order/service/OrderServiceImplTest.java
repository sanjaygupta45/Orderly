package com.orderflow.order.service;

import com.orderflow.order.dto.CreateOrderRequest;
import com.orderflow.order.dto.OrderItemRequest;
import com.orderflow.order.dto.OrderResponse;
import com.orderflow.order.model.Order;
import com.orderflow.order.repository.OrderRepository;
import com.orderflow.order.service.impl.OrderServiceImpl;
import com.orderflow.shared.common.outbox.OutboxEventPublisher;
import com.orderflow.shared.events.OrderCreatedEvent;
import com.orderflow.shared.events.RoutingKeys;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    OrderRepository orderRepository;
    @Mock
    OutboxEventPublisher outbox;

    MeterRegistry meterRegistry = new SimpleMeterRegistry();
    OrderServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new OrderServiceImpl(orderRepository, outbox, meterRegistry);
    }

    @Test
    void createOrder_computesTotal_savesPending_andEmitsOrderCreated() {
        CreateOrderRequest request = new CreateOrderRequest(1L, List.of(
                new OrderItemRequest("SKU-1", 2, new BigDecimal("100")),
                new OrderItemRequest("SKU-2", 1, new BigDecimal("50"))));

        OrderResponse response = service.createOrder(request);

        assertEquals("PENDING_PAYMENT", response.status());
        assertEquals(0, new BigDecimal("250").compareTo(response.totalAmount()));
        assertEquals(2, response.items().size());
        verify(orderRepository).save(any(Order.class));
        verify(outbox).save(eq(RoutingKeys.ORDER_CREATED), any(OrderCreatedEvent.class));
    }
}
