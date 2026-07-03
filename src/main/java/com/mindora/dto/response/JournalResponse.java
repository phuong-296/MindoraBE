package com.mindora.dto.response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record JournalResponse(
        UUID id,
        String title,
        String content,
        String moodValue,
        String[] tags,
        LocalDate entryDate,
        Instant createdAt,
        Instant updatedAt
) {}
