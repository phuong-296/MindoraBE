package com.mindora.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Phiên trò chuyện giữa user và AI Dora.
 * Mỗi user có thể có nhiều conversation; conversation có thể archive thay vì xóa.
 * updatedAt được tự động cập nhật mỗi lần có tin nhắn mới để sắp xếp "gần đây nhất".
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ai_conversations")
public class AiConversation extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 255)
    private String title;

    // true = ẩn khỏi danh sách nhưng không xóa, giữ lại lịch sử
    @Column(name = "is_archived")
    private Boolean isArchived = false;

    // Tin nhắn luôn được sắp xếp theo thứ tự thời gian tăng dần để hiển thị đúng luồng chat
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<Message> messages = new ArrayList<>();
}
