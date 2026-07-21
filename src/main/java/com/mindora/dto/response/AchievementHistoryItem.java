package com.mindora.dto.response;

import java.time.LocalDate;

/**
 * Một huy hiệu đã đạt được trong "Bộ sưu tập huy hiệu" — mỗi item ứng với 1 ngày user thực sự
 * có tiến bộ/vượt khó theo đánh giá của Gemini (xem DailyInsightServiceImpl.applyAndSave).
 * Vì achievement được AI sinh tự do theo ngữ cảnh (không phải catalog cố định), gallery chỉ liệt
 * kê những huy hiệu ĐÃ mở — không có khái niệm "còn khóa" cho từng huy hiệu cụ thể.
 */
public record AchievementHistoryItem(
        LocalDate date,
        String title,
        String description
) {}
