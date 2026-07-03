package com.mindora.dto.response;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String type,
        String title,
        String message,
        Boolean isRead,
        Instant createdAt
) {}
