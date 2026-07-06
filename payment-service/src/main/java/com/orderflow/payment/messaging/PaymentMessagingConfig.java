package com.orderflow.payment.messaging;

import com.orderflow.shared.events.Exchanges;
import com.orderflow.shared.events.RoutingKeys;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.amqp.core.Binding.DestinationType.QUEUE;

// payment-service listens only for inventory.reserved (its cue to charge).
@Configuration
public class PaymentMessagingConfig {

    public static final String PAYMENT_QUEUE = "payment.queue";
    public static final String PAYMENT_DLQ = "payment.queue.dlq";

    @Bean
    public Queue paymentQueue() {
        return QueueBuilder.durable(PAYMENT_QUEUE)
                .withArgument("x-dead-letter-exchange", Exchanges.ORDER_DLX)
                .withArgument("x-dead-letter-routing-key", PAYMENT_DLQ)
                .build();
    }

    @Bean
    public Queue paymentDlq() {
        return QueueBuilder.durable(PAYMENT_DLQ).build();
    }

    @Bean
    public Declarables paymentBindings() {
        return new Declarables(
                new Binding(PAYMENT_QUEUE, QUEUE, Exchanges.ORDER_EXCHANGE, RoutingKeys.INVENTORY_RESERVED, null),
                new Binding(PAYMENT_DLQ, QUEUE, Exchanges.ORDER_DLX, PAYMENT_DLQ, null));
    }
}
