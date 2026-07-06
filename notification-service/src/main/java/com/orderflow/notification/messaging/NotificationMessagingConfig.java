package com.orderflow.notification.messaging;

import com.orderflow.shared.events.Exchanges;
import com.orderflow.shared.events.RoutingKeys;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.amqp.core.Binding.DestinationType.QUEUE;

// notification-service reacts to the terminal order events.
@Configuration
public class NotificationMessagingConfig {

    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String NOTIFICATION_DLQ = "notification.queue.dlq";

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", Exchanges.ORDER_DLX)
                .withArgument("x-dead-letter-routing-key", NOTIFICATION_DLQ)
                .build();
    }

    @Bean
    public Queue notificationDlq() {
        return QueueBuilder.durable(NOTIFICATION_DLQ).build();
    }

    @Bean
    public Declarables notificationBindings() {
        return new Declarables(
                new Binding(NOTIFICATION_QUEUE, QUEUE, Exchanges.ORDER_EXCHANGE, RoutingKeys.ORDER_CONFIRMED, null),
                new Binding(NOTIFICATION_QUEUE, QUEUE, Exchanges.ORDER_EXCHANGE, RoutingKeys.ORDER_CANCELLED, null),
                new Binding(NOTIFICATION_DLQ, QUEUE, Exchanges.ORDER_DLX, NOTIFICATION_DLQ, null));
    }
}
