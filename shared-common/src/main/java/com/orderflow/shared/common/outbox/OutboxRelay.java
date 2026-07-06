package com.orderflow.shared.common.outbox;

import com.orderflow.shared.common.correlation.CorrelationId;
import com.orderflow.shared.events.Exchanges;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

// Background relay: every tick it grabs unpublished outbox rows and sends them to
// RabbitMQ, then marks them published. If a send fails the row stays unpublished
// and is retried next tick - so events are delivered at-least-once (consumers dedupe).
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnClass(RabbitTemplate.class)
public class OutboxRelay {

    private final OutboxEventRepository repository;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedDelayString = "${orderflow.outbox.poll-delay-ms:1000}")
    @Transactional
    public void publishPending() {
        List<OutboxEvent> pending = repository.findTop100ByPublishedFalseOrderByCreatedAtAsc();
        if (pending.isEmpty()) {
            return;
        }
        for (OutboxEvent event : pending) {
            try {
                rabbitTemplate.send(Exchanges.ORDER_EXCHANGE, event.getRoutingKey(), toMessage(event));
                event.setPublished(true);
                event.setPublishedAt(Instant.now());
            } catch (Exception ex) {
                // Leave it unpublished; next tick retries it.
                log.error("Failed to publish outbox event {} (will retry)", event.getEventId(), ex);
            }
        }
    }

    // Send the already-serialized JSON as-is so the Jackson converter does not
    // encode it a second time. Message id + correlation id ride along in headers.
    private Message toMessage(OutboxEvent event) {
        MessageProperties props = new MessageProperties();
        props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        props.setContentEncoding(StandardCharsets.UTF_8.name());
        props.setMessageId(event.getEventId());
        // Tell the consumer which class to deserialize into (Jackson type mapper reads this).
        props.setHeader("__TypeId__", event.getEventType());
        if (event.getCorrelationId() != null) {
            props.setHeader(CorrelationId.HEADER, event.getCorrelationId());
            props.setCorrelationId(event.getCorrelationId());
        }
        return new Message(event.getPayload().getBytes(StandardCharsets.UTF_8), props);
    }
}
