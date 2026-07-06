package com.orderflow.payment.messaging;

import com.orderflow.payment.service.PaymentService;
import com.orderflow.shared.events.InventoryReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@RabbitListener(queues = PaymentMessagingConfig.PAYMENT_QUEUE)
public class PaymentEventListener {

    private final PaymentService paymentService;

    @RabbitHandler
    public void handle(InventoryReservedEvent event) {
        paymentService.process(event);
    }

    @RabbitHandler(isDefault = true)
    public void handleUnknown(Object event) {
        log.warn("payment.queue received an unexpected message type: {}", event.getClass().getName());
    }
}
