package com.orderflow.notification.model;

// Why the notification was sent.
public enum NotificationType {
    CONFIRMATION,  // order confirmed
    FAILURE        // order failed / cancelled
}
