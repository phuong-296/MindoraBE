package com.mindora.service;

import com.mindora.dto.response.NotificationResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    void create(UUID userId, String type, String title, String message);
    void sendAlert(UUID userId, String title, String message);
    List<NotificationResponse> list(UUID userId);
    long unreadCount(UUID userId);
    void markAsRead(UUID userId, UUID notificationId);
    void markAllAsRead(UUID userId);
}
