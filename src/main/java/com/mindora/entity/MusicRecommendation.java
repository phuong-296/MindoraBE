package com.mindora.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Lưu lịch sử gợi ý nhạc được cá nhân hóa theo cảm xúc của user.
 * moodTag: tag cảm xúc tại thời điểm gợi ý (lấy từ mood log hoặc chat gần nhất).
 * reason: giải thích lý do gợi ý (có thể hiển thị cho user hoặc dùng để train model sau này).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "music_recommendations")
public class MusicRecommendation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private ContentLibrary content;

    @Column(name = "mood_tag", length = 50)
    private String moodTag;     // dựa trên cảm xúc gần nhất của user

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "recommended_at")
    private Instant recommendedAt = Instant.now();
}
