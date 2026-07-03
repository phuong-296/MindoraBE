package com.mindora.dto.response;

import java.util.UUID;

public record ContentResponse(
        UUID id,
        String title,
        String contentType,
        String description,
        String thumbnailUrl,
        String contentUrl,
        String moodTag,
        Integer durationMinutes,
        Boolean isSaved
) {}
