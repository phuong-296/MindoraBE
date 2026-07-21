package com.mindora.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * "Khoảnh khắc khám phá" mỗi ngày — 1 bản ghi/user/ngày (unique user_id+insight_date), sinh ra
 * từ việc Gemini phân tích TOÀN BỘ đoạn chat của user trong ngày đó. Chỉ tính 1 lần/ngày (xem
 * DailyInsightServiceImpl) — vừa tiết kiệm token, vừa tạo cảm giác "phần thưởng mỗi ngày" giống
 * cơ chế daily reward, đúng tinh thần khiến người dùng tò mò quay lại app mỗi ngày.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "daily_insights",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "insight_date"}))
public class DailyInsight extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "insight_date", nullable = false)
    private LocalDate insightDate;

    @Column(name = "mood_score")
    private Integer moodScore;

    @Column(length = 100)
    private String emotion;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String insight;

    @Column(name = "next_tip", columnDefinition = "TEXT")
    private String nextTip;

    @Column(name = "daily_title", length = 255)
    private String dailyTitle;

    @Column(name = "streak_title", length = 255)
    private String streakTitle;

    @Column(name = "pet_message", columnDefinition = "TEXT")
    private String petMessage;

    @Column(name = "xp_earned")
    private Integer xpEarned;

    @Column(name = "streak_increased")
    private Boolean streakIncreased;

    @Column(name = "tree_growth_delta")
    private Integer treeGrowthDelta;

    @Column(name = "achievement_unlocked")
    private Boolean achievementUnlocked;

    @Column(name = "achievement_title", length = 255)
    private String achievementTitle;

    @Column(name = "achievement_description", columnDefinition = "TEXT")
    private String achievementDescription;
}
