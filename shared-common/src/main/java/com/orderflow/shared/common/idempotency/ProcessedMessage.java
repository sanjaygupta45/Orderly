package com.orderflow.shared.common.idempotency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

// One row per event a service has already handled. The event id is the primary
// key, so a redelivered message can be recognised and skipped.
// Each service keeps this table in its own database.
@Entity
@Table(name = "processed_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessedMessage {

    @Id
    @Column(name = "event_id", length = 64)
    private String eventId;

    @Column(name = "event_type", length = 128)
    private String eventType;

    @Column(name = "processed_at")
    private Instant processedAt;
}
