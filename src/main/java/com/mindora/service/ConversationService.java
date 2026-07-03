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
    MessageResponse sendMessage(UUID userId, UUID conversationId, SendMessageRequest request);
    void clearMessages(UUID userId, UUID conversationId);
}
