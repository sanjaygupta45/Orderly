package com.orderly.product_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        @ExceptionHandler(ResponseStatusException.class)
        public ResponseEntity<ApiError> handleResponseStatusException(
                        ResponseStatusException ex, HttpServletRequest request) {
                log.warn("HTTP error: {} - {}", ex.getStatusCode(), ex.getReason());
                ApiError error = ApiError.builder()
                                .success(false)
                                .message(ex.getReason())
                                .status(ex.getStatusCode().value())
                                .timestamp(LocalDateTime.now())
                                .path(request.getRequestURI())
                                .build();
                return ResponseEntity.status(ex.getStatusCode()).body(error);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiError> handleValidationException(
                        MethodArgumentNotValidException ex, HttpServletRequest request) {
                String message = ex.getBindingResult().getFieldErrors().stream()
                                .findFirst()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                .orElse("Validation failed");
                log.warn("Validation error: {}", message);
                ApiError error = ApiError.builder()
                                .success(false)
                                .message(message)
                                .status(HttpStatus.BAD_REQUEST.value())
                                .timestamp(LocalDateTime.now())
                                .path(request.getRequestURI())
                                .build();
                return ResponseEntity.badRequest().body(error);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiError> handleGeneralException(
                        Exception ex, HttpServletRequest request) {
                log.error("Unexpected error", ex);
                ApiError error = ApiError.builder()
                                .success(false)
                                .message("An unexpected error occurred")
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .timestamp(LocalDateTime.now())
                                .path(request.getRequestURI())
                                .build();
                return ResponseEntity.internalServerError().body(error);
        }
}
