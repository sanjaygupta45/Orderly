package com.orderflow.inventory.service;

import com.orderflow.inventory.model.Inventory;
import com.orderflow.inventory.model.ReservationStatus;
import com.orderflow.inventory.model.StockReservation;
import com.orderflow.inventory.repository.InventoryRepository;
import com.orderflow.inventory.repository.StockReservationRepository;
import com.orderflow.shared.common.correlation.CorrelationId;
import com.orderflow.shared.common.idempotency.IdempotencyService;
import com.orderflow.shared.common.outbox.OutboxEventPublisher;
import com.orderflow.shared.events.InventoryFailedEvent;
import com.orderflow.shared.events.InventoryReleasedEvent;
import com.orderflow.shared.events.InventoryReservedEvent;
import com.orderflow.shared.events.OrderCreatedEvent;
import com.orderflow.shared.events.OrderLineItem;
import com.orderflow.shared.events.PaymentFailedEvent;
import com.orderflow.shared.events.RoutingKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// The saga side of inventory: reserve stock on order.created, release it on
// payment.failed. Both are idempotent and transactional; the caller
// (InventorySagaHandler) holds the Redisson lock across the transaction.
@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {

    private final InventoryRepository inventoryRepository;
    private final StockReservationRepository reservationRepository;
    private final IdempotencyService idempotency;
    private final OutboxEventPublisher outbox;
    private final CacheManager cacheManager;

    @Transactional
    public void reserveForOrder(OrderCreatedEvent event) {
        MDC.put(CorrelationId.MDC_KEY, event.getCorrelationId());
        try {
            if (!idempotency.isNew(event.getEventId(), "OrderCreated")) {
                return;
            }

            // sum the quantity per SKU (an order may list the same SKU more than once)
            Map<String, Integer> needed = new LinkedHashMap<>();
            for (OrderLineItem item : event.getItems()) {
                needed.merge(item.skuCode(), item.quantity(), Integer::sum);
            }

            // all-or-nothing: check every SKU has enough available first...
            String failure = null;
            Map<String, Inventory> found = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> line : needed.entrySet()) {
                Inventory inventory = inventoryRepository.findBySkuCode(line.getKey()).orElse(null);
                if (inventory == null || inventory.availableQuantity() < line.getValue()) {
                    failure = "Insufficient stock for " + line.getKey();
                    break;
                }
                found.put(line.getKey(), inventory);
            }

            if (failure == null) {
                // ...then apply the reservation
                needed.forEach((sku, qty) -> {
                    found.get(sku).reserve(qty);
                    reservationRepository.save(StockReservation.builder()
                            .orderId(event.getOrderId())
                            .skuCode(sku)
                            .quantity(qty)
                            .status(ReservationStatus.RESERVED)
                            .build());
                    evict(sku);
                });
                outbox.save(RoutingKeys.INVENTORY_RESERVED, InventoryReservedEvent.builder()
                        .orderId(event.getOrderId())
                        .userId(event.getUserId())
                        .amount(event.getTotalAmount())
                        .correlationId(event.getCorrelationId())
                        .build());
                log.info("Reserved stock for order {}", event.getOrderId());
            } else {
                outbox.save(RoutingKeys.INVENTORY_FAILED, InventoryFailedEvent.builder()
                        .orderId(event.getOrderId())
                        .reason(failure)
                        .correlationId(event.getCorrelationId())
                        .build());
                log.warn("Reservation failed for order {}: {}", event.getOrderId(), failure);
            }
        } finally {
            MDC.remove(CorrelationId.MDC_KEY);
        }
    }

    @Transactional
    public void releaseForOrder(PaymentFailedEvent event) {
        MDC.put(CorrelationId.MDC_KEY, event.getCorrelationId());
        try {
            if (!idempotency.isNew(event.getEventId(), "PaymentFailed")) {
                return;
            }
            List<StockReservation> reservations =
                    reservationRepository.findByOrderIdAndStatus(event.getOrderId(), ReservationStatus.RESERVED);
            for (StockReservation reservation : reservations) {
                inventoryRepository.findBySkuCode(reservation.getSkuCode()).ifPresent(inventory -> {
                    inventory.release(reservation.getQuantity());
                    evict(reservation.getSkuCode());
                });
                reservation.setStatus(ReservationStatus.RELEASED);
            }
            outbox.save(RoutingKeys.INVENTORY_RELEASED, InventoryReleasedEvent.builder()
                    .orderId(event.getOrderId())
                    .reason(event.getReason())
                    .correlationId(event.getCorrelationId())
                    .build());
            log.info("Released {} reservation(s) for order {}", reservations.size(), event.getOrderId());
        } finally {
            MDC.remove(CorrelationId.MDC_KEY);
        }
    }

    private void evict(String skuCode) {
        Cache cache = cacheManager.getCache("inventory");
        if (cache != null) {
            cache.evict(skuCode);
        }
    }
}
