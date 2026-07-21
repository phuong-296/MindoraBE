package com.mindora.dto.response;

public record AchievementInfo(
        boolean unlockedToday,
        String title,
        String description
) {}
