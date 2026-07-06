package com.orderflow.shared.common.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderflow.shared.common.correlation.CorrelationId;
import com.orderflow.shared.events.BaseEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

// Services call save(...) from inside their business transaction to hand an event
// to the outbox. Nothing goes to RabbitMQ here - OutboxRelay does that later.
@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    // MANDATORY so the outbox row is written in the caller's transaction.
    @Transactional(propagation = Propagation.MANDATORY)
    public void save(String routingKey, BaseEvent event) {
        if (event.getCorrelationId() == null) {
            event.setCorrelationId(CorrelationId.current());
        }
        repository.save(OutboxEvent.builder()
                .eventId(event.getEventId())
                .aggregateId(event.getOrderId())
                .routingKey(routingKey)
                .eventType(event.getClass().getName())
                .payload(serialize(event))
                .correlationId(event.getCorrelationId())
                .published(false)
                .createdAt(Instant.now())
                .build());
    }

    private String serialize(BaseEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize event " + event.getEventId(), e);
        }
    }
}
