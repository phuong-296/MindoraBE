package com.mindora.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class ContentLibrary extends AuditableEntity {

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

    // Link bài hát gốc trên Spotify (tùy chọn) — nút "Nghe trên Spotify" ở FE.
    // Khác với contentUrl: contentUrl là file audio phát được ngay (royalty-free),
    // còn spotifyUrl chỉ phát full khi người dùng đã đăng nhập Spotify trên trình duyệt của họ.
    @Column(name = "spotify_url", columnDefinition = "TEXT")
    private String spotifyUrl;

    // Video ID của MV/audio chính thức trên YouTube (chỉ 11 ký tự, vd "dQw4w9WgXcQ").
    // Nhúng qua YouTube iframe embed — phát được bài hát THẬT (có lời) miễn phí,
    // không cần đăng nhập tài khoản nào (khác spotifyUrl). Hợp pháp vì dùng tính năng
    // embed công khai do YouTube cung cấp, không tự host file nhạc có bản quyền.
    @Column(name = "youtube_id", length = 20)
    private String youtubeId;

    @Column(name = "mood_tag", length = 50)
    private String moodTag;      // calm | happy | sad | energy | sleep

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
}
