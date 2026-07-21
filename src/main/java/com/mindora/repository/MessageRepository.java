package com.mindora.repository;

import com.mindora.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    Page<Message> findByConversationIdOrderByCreatedAtAsc(UUID conversationId, Pageable pageable);

    // Lấy N tin nhắn mới nhất (dùng cho RAG history context)
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :convId ORDER BY m.createdAt DESC")
    Page<Message> findRecentMessages(@Param("convId") UUID convId, Pageable pageable);

    // Toàn bộ tin nhắn của 1 user (mọi conversation) trong khoảng thời gian — dùng cho DailyInsight
    // (phân tích cuộc trò chuyện "hôm nay"). Message không có FK trực tiếp tới User nên phải join
    // qua conversation.user.
    @Query("SELECT m FROM Message m WHERE m.conversation.user.id = :userId " +
           "AND m.createdAt >= :start AND m.createdAt < :end ORDER BY m.createdAt ASC")
    List<Message> findByUserIdAndCreatedAtBetween(
            @Param("userId") UUID userId, @Param("start") Instant start, @Param("end") Instant end);

    @Modifying
    void deleteByConversationId(UUID conversationId);
}
