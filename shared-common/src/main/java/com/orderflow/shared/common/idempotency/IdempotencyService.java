package com.orderflow.shared.common.idempotency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

// Helper every consumer uses to stay idempotent. Call isNew(...) at the start of
// handling an event: it returns true only the first time that event id is seen.
@Service
@Slf4j
@RequiredArgsConstructor
public class IdempotencyService {

    private final ProcessedMessageRepository repository;

    // MANDATORY: must run inside the consumer's transaction, so the "processed"
    // record commits together with the business change - never one without the other.
    // Under a race, the primary-key clash rolls back the second attempt; the message
    // is redelivered and then correctly seen as a duplicate.
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean isNew(String eventId, String eventType) {
        if (repository.existsById(eventId)) {
            log.debug("Duplicate event {} ({}) - skipping", eventId, eventType);
            return false;
        }
        repository.save(ProcessedMessage.builder()
                .eventId(eventId)
                .eventType(eventType)
                .processedAt(Instant.now())
                .build());
        return true;
    }
}
