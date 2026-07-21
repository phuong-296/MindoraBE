package com.mindora.service;

import com.mindora.dto.response.AchievementHistoryItem;
import com.mindora.dto.response.DailyInsightResponse;
import com.mindora.dto.response.WeeklySummaryResponse;

import java.util.List;
import java.util.UUID;

public interface DailyInsightService {

    /**
     * Lấy "khám phá hôm nay" của user — nếu chưa có cho ngày hôm nay và đã có đủ tin nhắn chat,
     * sẽ gọi Gemini phân tích và tính toán 1 lần rồi lưu lại (idempotent trong ngày).
     */
    DailyInsightResponse getTodayInsight(UUID userId);

    /** Toàn bộ huy hiệu user đã mở khóa được, mới nhất trước — dùng cho "Bộ sưu tập huy hiệu". */
    List<AchievementHistoryItem> getAchievementHistory(UUID userId);

    /** Tổng kết 7 ngày gần nhất (kể cả hôm nay) — dùng cho card "Tổng kết tuần". */
    WeeklySummaryResponse getWeeklySummary(UUID userId);
}
