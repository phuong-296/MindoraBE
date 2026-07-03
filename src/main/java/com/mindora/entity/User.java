package com.mindora.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity người dùng — bảng trung tâm của hệ thống.
 * Một User có thể có nhiều role (USER, EXPERT, ADMIN) thông qua bảng user_roles.
 * Mật khẩu được lưu dạng BCrypt hash, không bao giờ plain text.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "full_name")
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;       // BCrypt hashed

    @Column(name = "avatar_url")
    private String avatarUrl;

    // false = tài khoản bị khóa, isAccountNonLocked() sẽ trả false
    @Column(name = "is_active")
    private Boolean isActive = true;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    // Lazy load để tránh N+1 khi chỉ cần thông tin cơ bản của user
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserRole> userRoles = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserPreferences preferences;
}
