package com.mindora.dto.response;

import java.time.LocalDate;

/**
 * "Khám phá hôm nay" — response cho GET /api/dashboard/daily-insight.
 * streak/tree/xpInfo LUÔN có giá trị (số liệu cộng dồn của user, tồn tại kể cả chưa có insight
 * hôm nay). Các field còn lại (moodScore, emotion, summary, insight, nextTip, dailyTitle,
 * petMessage, achievement) chỉ có giá trị khi available=true.
 */
public record DailyInsightResponse(
        boolean available,
        LocalDate date,
        Integer moodScore,
        String emotion,
        String summary,
        String insight,
        String nextTip,
        String dailyTitle,
        String petMessage,
        AchievementInfo achievement,
        StreakInfo streak,
        TreeInfo tree,
        XpInfo xpInfo
) {}
