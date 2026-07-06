package com.orderflow.order.messaging;

import com.orderflow.shared.events.Exchanges;
import com.orderflow.shared.events.RoutingKeys;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.amqp.core.Binding.DestinationType.QUEUE;

// order-service's own queue + dead-letter queue, and the events it subscribes to.
// The exchange and DLX are declared once in shared-common (RabbitConfig).
@Configuration
public class OrderMessagingConfig {

    public static final String ORDER_QUEUE = "order.queue";
    public static final String ORDER_DLQ = "order.queue.dlq";

    @Bean
    public Queue orderQueue() {
        // messages that exhaust retries are dead-lettered to the shared DLX
        return QueueBuilder.durable(ORDER_QUEUE)
                .withArgument("x-dead-letter-exchange", Exchanges.ORDER_DLX)
                .withArgument("x-dead-letter-routing-key", ORDER_DLQ)
                .build();
    }

    @Bean
    public Queue orderDlq() {
        return QueueBuilder.durable(ORDER_DLQ).build();
    }

    // Bind the queue to the events order-service reacts to, and the DLQ to the DLX.
    @Bean
    public Declarables orderBindings() {
        return new Declarables(
                new Binding(ORDER_QUEUE, QUEUE, Exchanges.ORDER_EXCHANGE, RoutingKeys.PAYMENT_COMPLETED, null),
                new Binding(ORDER_QUEUE, QUEUE, Exchanges.ORDER_EXCHANGE, RoutingKeys.PAYMENT_FAILED, null),
                new Binding(ORDER_QUEUE, QUEUE, Exchanges.ORDER_EXCHANGE, RoutingKeys.INVENTORY_FAILED, null),
                new Binding(ORDER_DLQ, QUEUE, Exchanges.ORDER_DLX, ORDER_DLQ, null));
    }
}
