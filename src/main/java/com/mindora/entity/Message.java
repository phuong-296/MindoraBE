package com.mindora.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Một tin nhắn trong phiên trò chuyện AI.
 * role = USER: tin nhắn từ người dùng, có detectedEmotion và sentimentScore từ EmotionAnalysisService.
 * role = AI: phản hồi từ Dora (qua RAG pipeline), không phân tích cảm xúc.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private AiConversation conversation;

    // Lưu dạng STRING trong DB ("USER"/"AI") để dễ đọc và debug
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageRole role;        // USER | AI

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "detected_emotion", length = 50)
    private String detectedEmotion;  // happy | sad | anxious | angry | tired | neutral

    // Điểm cảm xúc: 0.00 (rất tiêu cực) → 1.00 (rất tích cực)
    @Column(name = "sentiment_score", precision = 3, scale = 2)
    private BigDecimal sentimentScore;  // 0.00 -> 1.00
}
