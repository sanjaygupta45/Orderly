package com.orderflow.inventory.messaging;

import com.orderflow.shared.events.Exchanges;
import com.orderflow.shared.events.RoutingKeys;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.amqp.core.Binding.DestinationType.QUEUE;

// inventory-service's queue + DLQ and the events it reacts to:
// order.created (reserve) and payment.failed (release / compensation).
@Configuration
public class InventoryMessagingConfig {

    public static final String INVENTORY_QUEUE = "inventory.queue";
    public static final String INVENTORY_DLQ = "inventory.queue.dlq";

    @Bean
    public Queue inventoryQueue() {
        return QueueBuilder.durable(INVENTORY_QUEUE)
                .withArgument("x-dead-letter-exchange", Exchanges.ORDER_DLX)
                .withArgument("x-dead-letter-routing-key", INVENTORY_DLQ)
                .build();
    }

    @Bean
    public Queue inventoryDlq() {
        return QueueBuilder.durable(INVENTORY_DLQ).build();
    }

    @Bean
    public Declarables inventoryBindings() {
        return new Declarables(
                new Binding(INVENTORY_QUEUE, QUEUE, Exchanges.ORDER_EXCHANGE, RoutingKeys.ORDER_CREATED, null),
                new Binding(INVENTORY_QUEUE, QUEUE, Exchanges.ORDER_EXCHANGE, RoutingKeys.PAYMENT_FAILED, null),
                new Binding(INVENTORY_DLQ, QUEUE, Exchanges.ORDER_DLX, INVENTORY_DLQ, null));
    }
}
