package com.mindora.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

/**
 * Tùy chọn cá nhân của từng user.
 * Được tạo tự động với giá trị mặc định khi user đăng ký (trong AuthServiceImpl).
 * Quan hệ 1-1 với User — mỗi user chỉ có 1 bộ preferences.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_preferences")
public class UserPreferences extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 20)
    private String language = "vi";

    // Dùng mảng PostgreSQL native để lưu nhiều thể loại nhạc yêu thích
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "favorite_music_genres", columnDefinition = "text[]")
    private String[] favoriteMusicGenres;

    // daily | weekly | never — tần suất gửi nhắc nhở
    @Column(name = "notification_frequency", length = 50)
    private String notificationFrequency = "daily";

    @Column(name = "dark_mode")
    private Boolean darkMode = false;

    @Column(name = "email_notifications")
    private Boolean emailNotifications = true;

    @Column(name = "push_notifications")
    private Boolean pushNotifications = true;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
