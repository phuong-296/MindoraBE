package com.mindora.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Danh sách nội dung user đã lưu yêu thích (bookmark).
 * Unique constraint (user_id, content_id) ngăn lưu trùng — service throw DuplicateResourceException nếu vi phạm.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_saved_content",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "content_id"}))
public class UserSavedContent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private ContentLibrary content;

    @Column(name = "saved_at")
    private Instant savedAt = Instant.now();
}
