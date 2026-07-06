package com.orderflow.inventory.messaging;

import com.orderflow.inventory.saga.InventorySagaHandler;
import com.orderflow.shared.events.OrderCreatedEvent;
import com.orderflow.shared.events.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

// One queue for the service; @RabbitHandler dispatches by event type.
// An exception here triggers retry, then the message is dead-lettered.
@Component
@Slf4j
@RequiredArgsConstructor
@RabbitListener(queues = InventoryMessagingConfig.INVENTORY_QUEUE)
public class InventoryEventListener {

    private final InventorySagaHandler saga;

    @RabbitHandler
    public void handle(OrderCreatedEvent event) {
        saga.onOrderCreated(event);
    }

    @RabbitHandler
    public void handle(PaymentFailedEvent event) {
        saga.onPaymentFailed(event);
    }

    @RabbitHandler(isDefault = true)
    public void handleUnknown(Object event) {
        log.warn("inventory.queue received an unexpected message type: {}", event.getClass().getName());
    }
}
