package com.orderflow.notification.service;

import com.orderflow.notification.dto.NotificationResponse;
import com.orderflow.notification.model.Notification;
import com.orderflow.notification.model.NotificationChannel;
import com.orderflow.notification.model.NotificationType;
import com.orderflow.notification.repository.NotificationRepository;
import com.orderflow.shared.common.correlation.CorrelationId;
import com.orderflow.shared.common.idempotency.IdempotencyService;
import com.orderflow.shared.common.outbox.OutboxEventPublisher;
import com.orderflow.shared.events.NotificationCreatedEvent;
import com.orderflow.shared.events.OrderCancelledEvent;
import com.orderflow.shared.events.OrderConfirmedEvent;
import com.orderflow.shared.events.RoutingKeys;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Terminal step of the saga: turn confirmed/cancelled orders into notifications.
// Generates one record per channel (email/SMS/push, all simulated) and stores the
// history. Idempotent so a redelivered event doesn't notify the customer twice.
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final IdempotencyService idempotency;
    private final OutboxEventPublisher outbox;
    private final MeterRegistry meterRegistry;

    @Transactional
    public void onOrderConfirmed(OrderConfirmedEvent event) {
        MDC.put(CorrelationId.MDC_KEY, event.getCorrelationId());
        try {
            if (!idempotency.isNew(event.getEventId(), "OrderConfirmed")) {
                return;
            }
            generate(event.getOrderId(), event.getUserId(), NotificationType.CONFIRMATION,
                    "Your order " + event.getOrderId() + " is confirmed. Thank you!",
                    event.getCorrelationId());
        } finally {
            MDC.remove(CorrelationId.MDC_KEY);
        }
    }

    @Transactional
    public void onOrderCancelled(OrderCancelledEvent event) {
        MDC.put(CorrelationId.MDC_KEY, event.getCorrelationId());
        try {
            if (!idempotency.isNew(event.getEventId(), "OrderCancelled")) {
                return;
            }
            generate(event.getOrderId(), event.getUserId(), NotificationType.FAILURE,
                    "Your order " + event.getOrderId() + " could not be completed: " + event.getReason(),
                    event.getCorrelationId());
        } finally {
            MDC.remove(CorrelationId.MDC_KEY);
        }
    }

    // fan out to every channel, store the history, then emit one audit event
    private void generate(String orderId, Long userId, NotificationType type, String content, String correlationId) {
        for (NotificationChannel channel : NotificationChannel.values()) {
            Notification notification = new Notification();
            notification.setOrderId(orderId);
            notification.setUserId(userId);
            notification.setChannel(channel);
            notification.setType(type);
            notification.setContent(content);
            notification.setStatus("SENT");
            notificationRepository.save(notification);
            // real delivery would call an email/SMS/push provider here
            meterRegistry.counter("orderflow.notifications.sent", "channel", channel.name(), "type", type.name()).increment();
            log.info("[{}] to user {} for order {}: {}", channel, userId, orderId, content);
        }
        outbox.save(RoutingKeys.NOTIFICATION_CREATED, NotificationCreatedEvent.builder()
                .orderId(orderId)
                .userId(userId)
                .channel("EMAIL,SMS,PUSH")
                .notificationType(type.name())
                .correlationId(correlationId)
                .build());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getByUserId(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(n.getOrderId(), n.getUserId(), n.getChannel().name(),
                n.getType().name(), n.getContent(), n.getStatus(), n.getCreatedAt());
    }
}
