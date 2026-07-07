package com.orderflow.inventory.service;

import com.orderflow.inventory.model.Inventory;
import com.orderflow.inventory.model.ReservationStatus;
import com.orderflow.inventory.model.StockReservation;
import com.orderflow.inventory.repository.InventoryRepository;
import com.orderflow.inventory.repository.StockReservationRepository;
import com.orderflow.shared.common.idempotency.IdempotencyService;
import com.orderflow.shared.common.outbox.OutboxEventPublisher;
import com.orderflow.shared.events.OrderCreatedEvent;
import com.orderflow.shared.events.OrderLineItem;
import com.orderflow.shared.events.PaymentFailedEvent;
import com.orderflow.shared.events.RoutingKeys;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;
import java.util.List;
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
class StockServiceTest {

    @Mock
    InventoryRepository inventoryRepository;
    @Mock
    StockReservationRepository reservationRepository;
    @Mock
    IdempotencyService idempotency;
    @Mock
    OutboxEventPublisher outbox;
    @Mock
    CacheManager cacheManager;

    MeterRegistry meterRegistry = new SimpleMeterRegistry();
    StockService service;

    @BeforeEach
    void setUp() {
        service = new StockService(inventoryRepository, reservationRepository, idempotency, outbox, cacheManager, meterRegistry);
    }

    private OrderCreatedEvent order(String id, String sku, int qty) {
        return OrderCreatedEvent.builder()
                .orderId(id).userId(1L).totalAmount(new BigDecimal("100"))
                .items(List.of(new OrderLineItem(sku, qty, new BigDecimal("50"))))
                .build();
    }

    @Test
    void reserve_withEnoughStock_reservesAndEmitsReserved() {
        when(idempotency.isNew(anyString(), anyString())).thenReturn(true);
        Inventory inventory = new Inventory("SKU-1", 10);
        when(inventoryRepository.findBySkuCode("SKU-1")).thenReturn(Optional.of(inventory));

        service.reserveForOrder(order("o1", "SKU-1", 2));

        assertEquals(2, inventory.getReservedQuantity());
        assertEquals(8, inventory.availableQuantity());
        verify(reservationRepository).save(any(StockReservation.class));
        verify(outbox).save(eq(RoutingKeys.INVENTORY_RESERVED), any());
    }

    @Test
    void reserve_withInsufficientStock_emitsFailed_andReservesNothing() {
        when(idempotency.isNew(anyString(), anyString())).thenReturn(true);
        when(inventoryRepository.findBySkuCode("SKU-1")).thenReturn(Optional.of(new Inventory("SKU-1", 1)));

        service.reserveForOrder(order("o2", "SKU-1", 5));

        verify(outbox).save(eq(RoutingKeys.INVENTORY_FAILED), any());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void reserve_duplicateEvent_isIgnored() {
        when(idempotency.isNew(anyString(), anyString())).thenReturn(false);

        service.reserveForOrder(order("o3", "SKU-1", 2));

        verifyNoInteractions(outbox);
        verify(inventoryRepository, never()).findBySkuCode(anyString());
    }

    @Test
    void release_returnsStock_andEmitsReleased() {
        when(idempotency.isNew(anyString(), anyString())).thenReturn(true);
        Inventory inventory = new Inventory("SKU-1", 10);
        inventory.reserve(3);
        StockReservation reservation = StockReservation.builder()
                .orderId("o4").skuCode("SKU-1").quantity(3).status(ReservationStatus.RESERVED).build();
        when(reservationRepository.findByOrderIdAndStatus("o4", ReservationStatus.RESERVED))
                .thenReturn(List.of(reservation));
        when(inventoryRepository.findBySkuCode("SKU-1")).thenReturn(Optional.of(inventory));

        service.releaseForOrder(PaymentFailedEvent.builder().orderId("o4").reason("declined").build());

        assertEquals(0, inventory.getReservedQuantity());
        assertEquals(ReservationStatus.RELEASED, reservation.getStatus());
        verify(outbox).save(eq(RoutingKeys.INVENTORY_RELEASED), any());
    }
}
