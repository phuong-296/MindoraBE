package com.mindora.dto.response;

import java.time.LocalDate;

/**
 * "Tổng kết tuần" — tổng hợp 7 ngày gần nhất (kể cả hôm nay) từ các DailyInsight đã có, để người
 * dùng thấy được tiến bộ theo THỜI GIAN thay vì chỉ nhìn con số của riêng hôm nay.
 * bestDay* có thể null nếu tuần này chưa có ngày nào được phân tích (chưa đủ chat).
 */
public record WeeklySummaryResponse(
        LocalDate fromDate,
        LocalDate toDate,
        int activeDays,
        int totalXpEarned,
        int achievementsUnlocked,
        int treeGrowthTotal,
        Double avgMoodScore,
        LocalDate bestDayDate,
        String bestDayTitle,
        Integer bestDayMoodScore,
        int currentStreak,
        int longestStreak
) {}
