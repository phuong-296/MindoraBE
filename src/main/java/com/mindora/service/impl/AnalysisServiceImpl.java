package com.mindora.service.impl;
import com.mindora.service.AnalysisService;
import com.mindora.service.NotificationService;

import com.mindora.dto.response.AnalysisResponse;
import com.mindora.entity.MentalHealthAnalysis;
import com.mindora.entity.MoodLog;
import com.mindora.exception.ResourceNotFoundException;
import com.mindora.repository.MentalHealthAnalysisRepository;
import com.mindora.repository.MoodLogRepository;
import com.mindora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Phân tích rủi ro sức khỏe tâm thần dựa trên điểm mood 7 ngày gần nhất.
 *
 * Thuật toán đánh giá rủi ro (rule-based từ avgScore):
 *   avgScore ≥ 5.0  → low      (0.15)
 *   avgScore ≥ 3.5  → medium   (0.40)
 *   avgScore ≥ 2.0  → high     (0.65)
 *   avgScore < 2.0  → critical (0.85) → tự động gửi cảnh báo
 */
@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private final MoodLogRepository moodLogRepository;
    private final MentalHealthAnalysisRepository analysisRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Transactional
    public AnalysisResponse runAnalysis(UUID userId) {
        LocalDate to   = LocalDate.now();
        LocalDate from = to.minusDays(6);  // 7 ngày gần nhất (từ ngày trước đến hôm nay)
        List<MoodLog> logs = moodLogRepository
                .findByUserIdAndLogDateBetweenOrderByLogDateAsc(userId, from, to);

        // Nếu không có dữ liệu, mặc định mood trung bình (4.0) để tránh false positive
        double avgScore = logs.isEmpty() ? 4.0
                : logs.stream().mapToInt(MoodLog::getMoodScore).average().orElse(4.0);

        String riskLevel;
        BigDecimal riskScore;
        if (avgScore >= 5.0) {
            riskLevel = "low";      riskScore = new BigDecimal("0.15");
        } else if (avgScore >= 3.5) {
            riskLevel = "medium";   riskScore = new BigDecimal("0.40");
        } else if (avgScore >= 2.0) {
            riskLevel = "high";     riskScore = new BigDecimal("0.65");
        } else {
            riskLevel = "critical"; riskScore = new BigDecimal("0.85");
        }

        MentalHealthAnalysis analysis = new MentalHealthAnalysis();
        analysis.setUser(userRepository.getReferenceById(userId));
        analysis.setSourceType("mood_log");
        analysis.setDepressionRiskScore(riskScore);
        analysis.setRiskLevel(riskLevel);
        analysis.setAiSummary(buildSummary(riskLevel, avgScore, logs.size()));
        analysis.setAlertSent(false);
        analysisRepository.save(analysis);

        // Gửi cảnh báo khẩn cấp khi rủi ro critical và đánh dấu để không gửi lại
        if ("critical".equals(riskLevel)) {
            notificationService.sendAlert(userId,
                "Cần chú ý sức khỏe tâm thần",
                "Dữ liệu gần đây cho thấy bạn đang trải qua giai đoạn khó khăn. " +
                "Hãy liên hệ chuyên gia hoặc gọi 1800 599 920.");
            analysis.setAlertSent(true);
            analysisRepository.save(analysis);
        }

        return toResponse(analysis);
    }

    @Transactional(readOnly = true)
    public AnalysisResponse getLatest(UUID userId) {
        MentalHealthAnalysis analysis = analysisRepository
                .findFirstByUserIdOrderByAnalyzedAtDesc(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Chưa có phân tích nào"));
        return toResponse(analysis);
    }

    @Transactional(readOnly = true)
    public List<AnalysisResponse> history(UUID userId) {
        return analysisRepository.findByUserIdOrderByAnalyzedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    /** Tạo mô tả tóm tắt kết quả phân tích bằng ngôn ngữ tự nhiên. */
    private String buildSummary(String riskLevel, double avgScore, int dataPoints) {
        return String.format(
            "Phân tích dựa trên %d điểm dữ liệu trong 7 ngày qua. " +
            "Điểm tâm trạng trung bình: %.1f/7. Mức độ rủi ro: %s.",
            dataPoints, avgScore, riskLevel);
    }

    private AnalysisResponse toResponse(MentalHealthAnalysis a) {
        return new AnalysisResponse(
                a.getId(), a.getSourceType(), a.getDepressionRiskScore(),
                a.getRiskLevel(), a.getAiSummary(), a.getAlertSent(), a.getAnalyzedAt());
    }
}
