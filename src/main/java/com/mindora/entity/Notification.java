package com.mindora.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Thông báo trong ứng dụng gửi đến user.
 * type phân loại để frontend hiển thị icon/màu phù hợp:
 *   - mood_reminder: nhắc nhở ghi mood hàng ngày
 *   - expert_alert: cảnh báo sức khỏe tâm thần từ phân tích AI
 *   - system: thông báo hệ thống (kết nối chuyên gia, cập nhật...)
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String type;      // mood_reminder | expert_alert | system

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    // false = chưa đọc, hiển thị badge đỏ trên giao diện
    @Column(name = "is_read")
    private Boolean isRead = false;
}
