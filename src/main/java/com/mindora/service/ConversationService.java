package com.mindora.service;

import com.mindora.dto.request.SendMessageRequest;
import com.mindora.dto.response.ConversationResponse;
import com.mindora.dto.response.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ConversationService {
    List<ConversationResponse> listConversations(UUID userId);
    ConversationResponse createConversation(UUID userId, String title);
    Page<MessageResponse> getMessages(UUID userId, UUID conversationId, Pageable pageable);
    /** Lấy N tin nhắn GẦN NHẤT (mới nhất trước) — dùng để suy luận context/emotion, khác với getMessages() vốn trả về thứ tự cũ→mới cho việc hiển thị lịch sử chat. */
    List<MessageResponse> getRecentMessages(UUID userId, UUID conversationId, int limit);
    MessageResponse sendMessage(UUID userId, UUID conversationId, SendMessageRequest request);
    void clearMessages(UUID userId, UUID conversationId);
}
