package com.mindora.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ConversationResponse(
        UUID id,
        String title,
        Boolean isArchived,
        Instant createdAt,
        Instant updatedAt
) {}
