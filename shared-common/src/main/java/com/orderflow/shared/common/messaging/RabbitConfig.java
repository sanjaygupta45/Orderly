package com.orderflow.shared.common.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderflow.shared.events.Exchanges;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

// Shared RabbitMQ setup: the topic exchange, the dead-letter exchange, and a JSON
// message converter. Individual services declare their own queues + bindings.
// @EnableScheduling here powers the OutboxRelay poller.
// Only active when spring-amqp is on the classpath, so a service that pulls in
// shared-common without messaging is unaffected.
@Configuration
@EnableScheduling
@ConditionalOnClass(RabbitTemplate.class)
public class RabbitConfig {

    // Main exchange: durable so it survives broker restarts, not auto-deleted.
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(Exchanges.ORDER_EXCHANGE, true, false);
    }

    // Dead-letter exchange: queues route poison/exhausted messages here.
    @Bean
    public TopicExchange orderDlx() {
        return new TopicExchange(Exchanges.ORDER_DLX, true, false);
    }

    @Bean
    public MessageConverter jacksonMessageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        // Producers stamp the event class in the __TypeId__ header; the consumer maps it
        // back to the concrete event type so @RabbitHandler can dispatch by type.
        // Trust only our events package (exact match - Spring AMQP does not glob "*").
        typeMapper.setTrustedPackages("com.orderflow.shared.events", "java.util", "java.lang");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    // Our own template so producers use the JSON converter too.
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
