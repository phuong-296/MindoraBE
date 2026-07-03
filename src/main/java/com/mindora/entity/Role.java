package com.mindora.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Vai trò trong hệ thống phân quyền.
 * 3 role cố định: USER (người dùng thông thường), EXPERT (chuyên gia), ADMIN (quản trị viên).
 * Dữ liệu được seed sẵn vào DB khi khởi động; không tạo role động ở runtime.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String name;   // USER | ADMIN | EXPERT

    private String description;
}
