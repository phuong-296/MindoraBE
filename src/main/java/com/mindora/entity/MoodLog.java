package com.mindora.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Nhật ký tâm trạng hàng ngày của user.
 * Unique constraint (user_id, log_date) đảm bảo mỗi ngày chỉ có 1 bản ghi — dùng upsert khi log.
 * moodScore: 1 = rất tệ (angry) → 7 = rất vui (loved); dùng để tính trung bình cho phân tích rủi ro.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "mood_logs",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "log_date"}))
public class MoodLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "mood_score", nullable = false)
    private Integer moodScore;   // 1 (angry) -> 7 (loved)

    @Column(name = "mood_emoji", length = 20)
    private String moodEmoji;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate = LocalDate.now();
}
