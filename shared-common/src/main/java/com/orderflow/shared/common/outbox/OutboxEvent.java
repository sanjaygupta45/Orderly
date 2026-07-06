package com.orderflow.shared.common.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

// Transactional outbox row. A service saves its business change AND this row in
// the SAME transaction, so the intent to publish is as durable as the data.
// OutboxRelay publishes it to RabbitMQ afterwards. This removes the "dual write"
// risk where the DB commits but the broker never hears about it (or vice versa).
@Entity
@Table(name = "outbox_event",
        indexes = @Index(name = "idx_outbox_unpublished", columnList = "published, created_at"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", length = 64, nullable = false, unique = true)
    private String eventId;

    @Column(name = "aggregate_id", length = 64)
    private String aggregateId;      // the orderId this event belongs to

    @Column(name = "routing_key", length = 64, nullable = false)
    private String routingKey;

    @Column(name = "event_type", length = 256, nullable = false)
    private String eventType;        // event class name; sent as the RabbitMQ type header

    @Lob
    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;          // event serialized to JSON

    @Column(name = "correlation_id", length = 64)
    private String correlationId;

    @Column(name = "published", nullable = false)
    @Builder.Default
    private boolean published = false;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "published_at")
    private Instant publishedAt;
}
