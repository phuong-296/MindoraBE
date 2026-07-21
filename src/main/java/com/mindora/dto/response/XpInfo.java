package com.mindora.dto.response;

public record XpInfo(
        int totalXp,
        int level,
        int xpEarnedToday,
        int xpIntoLevel,
        int xpForNextLevel
) {}
