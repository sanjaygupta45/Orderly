package com.orderflow.order.messaging;

import com.orderflow.order.saga.OrderSagaHandler;
import com.orderflow.shared.events.InventoryFailedEvent;
import com.orderflow.shared.events.PaymentCompletedEvent;
import com.orderflow.shared.events.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

// One queue for the service; @RabbitHandler dispatches by event type (resolved from
// the __TypeId__ header). An exception here triggers retry, then the DLQ.
@Component
@Slf4j
@RequiredArgsConstructor
@RabbitListener(queues = OrderMessagingConfig.ORDER_QUEUE)
public class OrderEventListener {

    private final OrderSagaHandler saga;

    @RabbitHandler
    public void handle(PaymentCompletedEvent event) {
        saga.onPaymentCompleted(event);
    }

    @RabbitHandler
    public void handle(PaymentFailedEvent event) {
        saga.onPaymentFailed(event);
    }

    @RabbitHandler
    public void handle(InventoryFailedEvent event) {
        saga.onInventoryFailed(event);
    }

    @RabbitHandler(isDefault = true)
    public void handleUnknown(Object event) {
        log.warn("order.queue received an unexpected message type: {}", event.getClass().getName());
    }
}
