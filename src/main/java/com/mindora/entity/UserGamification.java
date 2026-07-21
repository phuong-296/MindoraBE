package com.mindora.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Trạng thái "gây nghiện tích cực" của user — 1 bản ghi/user, cộng dồn theo thời gian.
 * Được cập nhật mỗi khi DailyInsightService tính insight cho 1 ngày mới (xem DailyInsight).
 * Toàn bộ số liệu ở đây do BACKEND tính toán xác định (streak/level/tree) — Gemini chỉ gợi ý
 * xp/tree_growth thô, không được toàn quyền quyết định số liệu cuối cùng (tương tự cách mood
 * luôn do Gemini xác định nhưng crisis/nhạc vẫn có lưới an toàn riêng ở RagServiceImpl).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_gamification",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id"}))
public class UserGamification extends AuditableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer xp = 0;

    @Column(nullable = false)
    private Integer level = 1;

    @Column(name = "current_streak", nullable = false)
    private Integer currentStreak = 0;

    @Column(name = "longest_streak", nullable = false)
    private Integer longestStreak = 0;

    /** Ngày gần nhất user được tính là "hoạt động" (đã có insight hôm đó) — dùng để tính streak. */
    @Column(name = "last_active_date")
    private LocalDate lastActiveDate;

    /** % tăng trưởng của "cây cảm xúc" hiện tại — reset về phần dư khi chạm mốc 100 (lên cây mới). */
    @Column(name = "tree_growth_percent", nullable = false)
    private Integer treeGrowthPercent = 0;

    /** Số cây đã "trưởng thành" (đạt 100%) — mốc thành tựu dài hạn, không reset. */
    @Column(name = "tree_count", nullable = false)
    private Integer treeCount = 0;
}
