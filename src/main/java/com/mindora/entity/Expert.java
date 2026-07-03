package com.mindora.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Hồ sơ chuyên gia tư vấn tâm lý.
 * Quan hệ 1-1 với User: tài khoản Expert vừa có quyền USER vừa có quyền EXPERT.
 * isVerified = false khi mới đăng ký; admin cần xác minh trước khi hiện ra cho user thấy.
 * isOnline: trạng thái online theo thời gian thực (có thể cập nhật qua API).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "experts")
public class Expert extends BaseEntity {

    // Unique: mỗi User chỉ có tối đa 1 hồ sơ chuyên gia
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 255)
    private String specialization;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 255)
    private String location;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    // precision=2, scale=1 → VD: 4.5, tối đa 9.9
    @Column(precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "is_online")
    private Boolean isOnline = false;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
