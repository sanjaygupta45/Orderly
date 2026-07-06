package com.orderflow.notification.messaging;

import com.orderflow.notification.service.NotificationService;
import com.orderflow.shared.events.OrderCancelledEvent;
import com.orderflow.shared.events.OrderConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@RabbitListener(queues = NotificationMessagingConfig.NOTIFICATION_QUEUE)
public class NotificationEventListener {

    private final NotificationService notificationService;

    @RabbitHandler
    public void handle(OrderConfirmedEvent event) {
        notificationService.onOrderConfirmed(event);
    }

    @RabbitHandler
    public void handle(OrderCancelledEvent event) {
        notificationService.onOrderCancelled(event);
    }

    @RabbitHandler(isDefault = true)
    public void handleUnknown(Object event) {
        log.warn("notification.queue received an unexpected message type: {}", event.getClass().getName());
    }
}
