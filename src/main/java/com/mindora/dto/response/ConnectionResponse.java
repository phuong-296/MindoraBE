package com.mindora.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ConnectionResponse(
        UUID id,
        UUID expertId,
        String expertName,
        UUID userId,
        String userName,
        String status,
        String triggerReason,
        String notes,
        Instant requestedAt
) {}
