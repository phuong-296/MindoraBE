package com.mindora.dto.response;

public record StreakInfo(
        int currentStreak,
        int longestStreak,
        boolean increasedToday,
        String title
) {}
