package com.orderflow.shared.common.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    // Oldest-first batch of events still waiting to be published.
    List<OutboxEvent> findTop100ByPublishedFalseOrderByCreatedAtAsc();
}
