package com.orderflow.payment.service;

import com.orderflow.payment.model.PaymentStatus;
import com.orderflow.payment.repository.PaymentRepository;
import com.orderflow.payment.simulation.PaymentSimulator;
import com.orderflow.shared.common.idempotency.IdempotencyService;
import com.orderflow.shared.common.outbox.OutboxEventPublisher;
import com.orderflow.shared.events.InventoryReservedEvent;
import com.orderflow.shared.events.RoutingKeys;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    PaymentRepository paymentRepository;
    @Mock
    IdempotencyService idempotency;
    @Mock
    OutboxEventPublisher outbox;
    @Mock
    PaymentSimulator simulator;

    MeterRegistry meterRegistry = new SimpleMeterRegistry();
    PaymentService service;

    @BeforeEach
    void setUp() {
        service = new PaymentService(paymentRepository, idempotency, outbox, simulator, meterRegistry);
    }

    private InventoryReservedEvent event() {
        return InventoryReservedEvent.builder().orderId("o1").userId(1L).amount(new BigDecimal("100")).build();
    }

    @Test
    void successfulCharge_emitsPaymentCompleted() {
        when(idempotency.isNew(anyString(), anyString())).thenReturn(true);
        when(simulator.decide(any())).thenReturn(PaymentStatus.SUCCESS);

        service.process(event());

        verify(outbox).save(eq(RoutingKeys.PAYMENT_COMPLETED), any());
    }

    @Test
    void declinedCharge_emitsPaymentFailed() {
        when(idempotency.isNew(anyString(), anyString())).thenReturn(true);
        when(simulator.decide(any())).thenReturn(PaymentStatus.FAILED);

        service.process(event());

        verify(outbox).save(eq(RoutingKeys.PAYMENT_FAILED), any());
    }
}
