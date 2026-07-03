package com.mindora.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Nhật ký cảm xúc tự do của user.
 * Khi tạo/cập nhật có moodValue, JournalService sẽ tự động upsert MoodLog tương ứng
 * để đồng bộ dữ liệu tâm trạng giữa nhật ký và biểu đồ mood.
 * tags: lưu dạng mảng PostgreSQL (text[]) để tìm kiếm linh hoạt.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "journal_entries")
public class JournalEntry extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "mood_value", length = 20)
    private String moodValue;   // loved | happy | neutral | sad | anxious | angry

    // Dùng @JdbcTypeCode(SqlTypes.ARRAY) để Hibernate map sang kiểu mảng native của PostgreSQL
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private String[] tags;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate = LocalDate.now();

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
