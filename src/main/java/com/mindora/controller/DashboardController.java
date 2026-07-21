package com.mindora.controller;

import com.mindora.dto.response.AchievementHistoryItem;
import com.mindora.dto.response.DailyInsightResponse;
import com.mindora.dto.response.WeeklySummaryResponse;
import com.mindora.security.UserPrincipal;
import com.mindora.service.DailyInsightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DailyInsightService dailyInsightService;

    /**
     * "Khám phá hôm nay" — tính (hoặc lấy lại nếu đã có) insight/gamification của ngày hôm nay.
     * available=false khi user chưa chat đủ trong ngày; FE nên hiển thị lời mời trò chuyện thay
     * vì báo lỗi.
     */
    @GetMapping("/daily-insight")
    public ResponseEntity<DailyInsightResponse> getDailyInsight(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(dailyInsightService.getTodayInsight(principal.getId()));
    }

    /** "Bộ sưu tập huy hiệu" — toàn bộ achievement đã mở khóa, mới nhất trước. */
    @GetMapping("/achievements")
    public ResponseEntity<List<AchievementHistoryItem>> getAchievements(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(dailyInsightService.getAchievementHistory(principal.getId()));
    }

    /** "Tổng kết tuần" — tổng hợp 7 ngày gần nhất. */
    @GetMapping("/weekly-summary")
    public ResponseEntity<WeeklySummaryResponse> getWeeklySummary(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(dailyInsightService.getWeeklySummary(principal.getId()));
    }
}
