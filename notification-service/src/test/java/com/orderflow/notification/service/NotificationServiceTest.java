package com.orderflow.notification.service;

import com.orderflow.notification.model.Notification;
import com.orderflow.notification.repository.NotificationRepository;
import com.orderflow.shared.common.idempotency.IdempotencyService;
import com.orderflow.shared.common.outbox.OutboxEventPublisher;
import com.orderflow.shared.events.OrderConfirmedEvent;
import com.orderflow.shared.events.RoutingKeys;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    NotificationRepository notificationRepository;
    @Mock
    IdempotencyService idempotency;
    @Mock
    OutboxEventPublisher outbox;

    MeterRegistry meterRegistry = new SimpleMeterRegistry();
    NotificationService service;

    @BeforeEach
    void setUp() {
        service = new NotificationService(notificationRepository, idempotency, outbox, meterRegistry);
    }

    @Test
    void orderConfirmed_generatesOneNotificationPerChannel() {
        when(idempotency.isNew(anyString(), anyString())).thenReturn(true);

        service.onOrderConfirmed(OrderConfirmedEvent.builder().orderId("o1").userId(1L).build());

        // EMAIL + SMS + PUSH
        verify(notificationRepository, times(3)).save(any(Notification.class));
        verify(outbox).save(eq(RoutingKeys.NOTIFICATION_CREATED), any());
    }

    @Test
    void duplicateEvent_isIgnored() {
        when(idempotency.isNew(anyString(), anyString())).thenReturn(false);

        service.onOrderConfirmed(OrderConfirmedEvent.builder().orderId("o1").userId(1L).build());

        verifyNoInteractions(notificationRepository, outbox);
    }
}
