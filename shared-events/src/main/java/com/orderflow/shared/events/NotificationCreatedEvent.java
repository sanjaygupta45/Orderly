package com.orderflow.shared.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

// Published by notification-service after it stores/sends a notification.
// Useful for audit dashboards and business metrics.
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class NotificationCreatedEvent extends BaseEvent {
    private Long userId;
    private String channel;            // EMAIL / SMS / PUSH
    private String notificationType;   // CONFIRMATION / FAILURE
}
