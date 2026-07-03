package com.mindora.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record MoodLogResponse(
        UUID id,
        Integer moodScore,
        String moodEmoji,
        String note,
        LocalDate logDate
) {}
