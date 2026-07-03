package com.mindora.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

/**
 * Thư viện nội dung chữa lành (nhạc, video, bài viết, bài tập thể dục).
 * moodTag giúp ContentService lọc nội dung phù hợp với tâm trạng hiện tại của user.
 * isActive = false để ẩn nội dung mà không cần xóa (soft delete).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "content_library")
public class ContentLibrary extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType;  // music | video | article | exercise

    @Column(columnDefinition = "TEXT")
    private String description;

    // TEXT thay vì VARCHAR để chứa URL dài (YouTube embed, S3...)
    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(name = "content_url", columnDefinition = "TEXT")
    private String contentUrl;

    @Column(name = "mood_tag", length = 50)
    private String moodTag;      // calm | happy | sad | energy | sleep

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
