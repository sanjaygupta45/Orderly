package com.orderly.order_service.exception;

import java.time.LocalDateTime;

/**
 * Structured error response for API errors.
 */
public record ApiError(
            int status,
            String message,
            LocalDateTime timestamp,
            String path) {
      public ApiError(int status, String message, String path) {
            this(status, message, LocalDateTime.now(), path);
      }
}
