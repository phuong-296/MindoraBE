package com.mindora.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

/**
 * Lớp cha trừu tượng cho tất cả entity — cung cấp id và createdAt.
 * @MappedSuperclass: các trường sẽ được ánh xạ vào bảng con, không tạo bảng riêng.
 * @EntityListeners(AuditingEntityListener.class): kích hoạt tự động ghi createdAt khi insert.
 * ID dùng UUID v4 để tránh xung đột khi scale ngang hoặc merge data từ nhiều nguồn.
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Tự động gán khi entity được persist lần đầu, không cho phép cập nhật sau đó
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
