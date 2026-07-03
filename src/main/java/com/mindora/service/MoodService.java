package com.mindora.service;

import com.mindora.dto.request.MoodLogRequest;
import com.mindora.dto.response.MoodLogResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MoodService {
    MoodLogResponse logMood(UUID userId, MoodLogRequest req);
    List<MoodLogResponse> getWeek(UUID userId);
    List<MoodLogResponse> getRange(UUID userId, LocalDate from, LocalDate to);
}
