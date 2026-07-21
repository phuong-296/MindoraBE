package com.mindora.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

/**
 * Lớp cha cho entity có tracking thời gian cập nhật.
 * Kế thừa id + createdAt từ BaseEntity, bổ sung updatedAt tự động qua JPA Auditing.
 * Dùng cho các bảng có dữ liệu thay đổi sau khi insert (User, Conversation, ...).
 */
@Getter
@Setter
@MappedSuperclass
public abstract class AuditableEntity extends BaseEntity {

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
