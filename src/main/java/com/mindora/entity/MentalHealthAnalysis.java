package com.mindora.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Kết quả phân tích sức khỏe tâm thần — tính toán từ dữ liệu mood trong 7 ngày.
 * depressionRiskScore: 0.00 (không rủi ro) → 1.00 (rủi ro cao).
 * Khi riskLevel = "critical", hệ thống tự gửi cảnh báo tới user và đặt alertSent = true.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "mental_health_analyses")
public class MentalHealthAnalysis extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "source_type", nullable = false, length = 50)
    private String sourceType;   // journal | chat | mood_log

    // precision=3, scale=2 → lưu được 0.00 đến 9.99 (dùng 0.00-1.00)
    @Column(name = "depression_risk_score", nullable = false, precision = 3, scale = 2)
    private BigDecimal depressionRiskScore;

    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel;    // low | medium | high | critical

    @Column(name = "ai_summary", columnDefinition = "TEXT")
    private String aiSummary;

    // Đánh dấu đã gửi cảnh báo để tránh spam thông báo lặp lại
    @Column(name = "alert_sent")
    private Boolean alertSent = false;

    @Column(name = "analyzed_at")
    private Instant analyzedAt = Instant.now();
}
