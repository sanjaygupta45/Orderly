package com.orderflow.notification.controller;

import com.orderflow.notification.dto.NotificationResponse;
import com.orderflow.notification.service.NotificationService;
import com.orderflow.shared.common.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Notification history for a user.
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> byUser(@RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(notificationService.getByUserId(userId)));
    }
}
