package com.orderflow.order.service.impl;

import com.orderflow.order.dto.CreateOrderRequest;
import com.orderflow.order.dto.OrderItemRequest;
import com.orderflow.order.dto.OrderItemResponse;
import com.orderflow.order.dto.OrderResponse;
import com.orderflow.order.model.Order;
import com.orderflow.order.model.OrderItem;
import com.orderflow.order.model.OrderStatus;
import com.orderflow.order.repository.OrderRepository;
import com.orderflow.order.service.OrderService;
import com.orderflow.shared.common.outbox.OutboxEventPublisher;
import com.orderflow.shared.events.OrderCreatedEvent;
import com.orderflow.shared.events.OrderLineItem;
import com.orderflow.shared.events.RoutingKeys;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventPublisher outbox;
    private final MeterRegistry meterRegistry;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setUserId(request.userId());
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        BigDecimal total = BigDecimal.ZERO;
        List<OrderLineItem> eventItems = new ArrayList<>();
        for (OrderItemRequest line : request.items()) {
            OrderItem item = new OrderItem();
            item.setSkuCode(line.skuCode());
            item.setQuantity(line.quantity());
            item.setUnitPrice(line.unitPrice());
            order.addItem(item);

            total = total.add(line.unitPrice().multiply(BigDecimal.valueOf(line.quantity())));
            eventItems.add(new OrderLineItem(line.skuCode(), line.quantity(), line.unitPrice()));
        }
        order.setTotalAmount(total);
        orderRepository.save(order);

        // Save the event in the SAME transaction (outbox). The relay publishes it after commit,
        // so we never end up with a saved order and no event (or vice versa).
        outbox.save(RoutingKeys.ORDER_CREATED, OrderCreatedEvent.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .totalAmount(total)
                .items(eventItems)
                .build());

        meterRegistry.counter("orderflow.orders", "status", "created").increment();
        log.info("Order {} created (PENDING_PAYMENT), total={}", order.getOrderId(), total);
        return toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(i -> new OrderItemResponse(i.getSkuCode(), i.getQuantity(), i.getUnitPrice()))
                .toList();
        return new OrderResponse(
                order.getOrderId(),
                order.getUserId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getFailureReason(),
                items,
                order.getCreatedAt(),
                order.getUpdatedAt());
    }
}
