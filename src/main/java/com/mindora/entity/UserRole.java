package com.mindora.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Bảng trung gian ánh xạ quan hệ nhiều-nhiều giữa User và Role.
 * Unique constraint (user_id, role_id) tránh gán trùng role cho cùng 1 user.
 * assignedAt ghi lại thời điểm được gán role để audit log.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_roles",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"}))
public class UserRole extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "assigned_at")
    private Instant assignedAt = Instant.now();
}
