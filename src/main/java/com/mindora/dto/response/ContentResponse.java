package com.mindora.dto.response;

import java.util.UUID;

public record ContentResponse(
        UUID id,
        String title,
        String contentType,
        String description,
        String thumbnailUrl,
        String contentUrl,
        String spotifyUrl,
        String youtubeId,
        String moodTag,
        Integer durationMinutes
) {}
