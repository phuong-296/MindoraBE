package com.mindora.service.impl;
import com.mindora.service.ConversationService;
import com.mindora.service.EmotionAnalysisService;

import com.mindora.dto.request.SendMessageRequest;
import com.mindora.dto.response.ConversationResponse;
import com.mindora.dto.response.MessageResponse;
import com.mindora.entity.AiConversation;
import com.mindora.entity.Message;
import com.mindora.entity.MessageRole;
import com.mindora.exception.ResourceNotFoundException;
import com.mindora.repository.AiConversationRepository;
import com.mindora.repository.ContentLibraryRepository;
import com.mindora.repository.MessageRepository;
import com.mindora.repository.UserRepository;
import com.mindora.util.SongMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Quản lý phiên trò chuyện và tin nhắn với AI Dora.
 * sendMessage() phân tích cảm xúc tự động cho tin nhắn của user trước khi lưu.
 * Mọi thao tác đều kiểm tra quyền sở hữu (requireOwnedConversation) để tránh user truy cập conversation của người khác.
 */
@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final AiConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final EmotionAnalysisService emotionService;
    private final ContentLibraryRepository contentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> listConversations(UUID userId) {
        // Chỉ trả về conversation chưa archive, sắp xếp mới nhất lên đầu
        return conversationRepository
                .findByUserIdAndIsArchivedFalseOrderByUpdatedAtDesc(userId)
                .stream()
                .map(this::toConversationResponse)
                .toList();
    }

    @Override
    @Transactional
    public ConversationResponse createConversation(UUID userId, String title) {
        AiConversation conv = new AiConversation();
        // getReferenceById() tạo proxy thay vì query DB — đủ để set FK
        conv.setUser(userRepository.getReferenceById(userId));
        conv.setTitle(title != null ? title : "Cuộc trò chuyện mới");
        conversationRepository.save(conv);
        return toConversationResponse(conv);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponse> getMessages(UUID userId, UUID conversationId, Pageable pageable) {
        requireOwnedConversation(userId, conversationId);
        return messageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversationId, pageable)
                .map(this::toMessageResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getRecentMessages(UUID userId, UUID conversationId, int limit) {
        requireOwnedConversation(userId, conversationId);
        // findRecentMessages sắp xếp DESC (mới nhất trước) — đúng ý nghĩa "gần nhất",
        // khác với getMessages() (ASC, dùng để hiển thị lịch sử theo trình tự thời gian).
        return messageRepository
                .findRecentMessages(conversationId, org.springframework.data.domain.PageRequest.of(0, limit))
                .map(this::toMessageResponse)
                .getContent();
    }

    @Override
    @Transactional
    public MessageResponse sendMessage(UUID userId, UUID conversationId, SendMessageRequest request) {
        AiConversation conv = requireOwnedConversation(userId, conversationId);

        Message message = new Message();
        message.setConversation(conv);
        message.setRole(MessageRole.valueOf(request.getRole().toUpperCase()));
        message.setContent(request.getContent());

        // Phân tích cảm xúc chỉ khi là tin nhắn của user (không phân tích response AI)
        if (message.getRole() == MessageRole.USER) {
            String emotion = emotionService.detectEmotion(request.getContent());
            message.setDetectedEmotion(emotion);
            message.setSentimentScore(emotionService.getSentimentScore(emotion));
        }

        messageRepository.save(message);
        // save(conv) cập nhật updatedAt để conversation hiện lên đầu danh sách "gần đây nhất"
        conversationRepository.save(conv);
        return toMessageResponse(message);
    }

    @Override
    @Transactional
    public void clearMessages(UUID userId, UUID conversationId) {
        requireOwnedConversation(userId, conversationId);
        messageRepository.deleteByConversationId(conversationId);
    }

    /**
     * Tìm conversation và kiểm tra đúng owner — ném 404 nếu không tìm thấy hoặc không phải của userId.
     * Pattern này ngăn IDOR (Insecure Direct Object Reference).
     */
    private AiConversation requireOwnedConversation(UUID userId, UUID conversationId) {
        return conversationRepository.findById(conversationId)
                .filter(c -> c.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy conversation"));
    }

    private ConversationResponse toConversationResponse(AiConversation c) {
        return new ConversationResponse(
                c.getId(), c.getTitle(), c.getIsArchived(), c.getCreatedAt(), c.getUpdatedAt());
    }

    private MessageResponse toMessageResponse(Message m) {
        return new MessageResponse(
                m.getId(),
                m.getRole().name().toLowerCase(),
                m.getContent(),
                m.getDetectedEmotion(),
                m.getSentimentScore(),
                m.getCreatedAt(),
                SongMapper.resolve(m.getSongIds(), contentRepository));
    }
}
