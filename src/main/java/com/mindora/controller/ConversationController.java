package com.mindora.controller;

import com.mindora.dto.request.SendMessageRequest;
import com.mindora.dto.response.ChatResponse;
import com.mindora.dto.response.ConversationResponse;
import com.mindora.dto.response.MessageResponse;
import com.mindora.dto.response.PageResponse;
import com.mindora.entity.MessageRole;
import com.mindora.security.UserPrincipal;
import com.mindora.service.ConversationService;
import com.mindora.service.RagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;
    private final RagService          ragService;

    @GetMapping
    public ResponseEntity<List<ConversationResponse>> list(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(conversationService.listConversations(principal.getId()));
    }

    @PostMapping
    public ResponseEntity<ConversationResponse> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody(required = false) Map<String, String> body) {
        String title = body != null ? body.get("title") : null;
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(conversationService.createConversation(principal.getId(), title));
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<PageResponse<MessageResponse>> messages(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        var result = conversationService.getMessages(
                principal.getId(), conversationId, PageRequest.of(page, size));
        return ResponseEntity.ok(PageResponse.of(result, m -> m));
    }

    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<MessageResponse> send(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID conversationId,
            @Valid @RequestBody SendMessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(conversationService.sendMessage(principal.getId(), conversationId, request));
    }

    @DeleteMapping("/{conversationId}/messages")
    public ResponseEntity<Void> clear(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID conversationId) {
        conversationService.clearMessages(principal.getId(), conversationId);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/conversations/{id}/chat
     * Endpoint RAG: user gửi 1 tin → Dora tự động phản hồi dựa trên knowledge base.
     * Body: { "content": "Tôi đang rất lo lắng..." }
     * Response: { userMessage, aiResponse, retrievedSources }
     */
    @PostMapping("/{conversationId}/chat")
    public ResponseEntity<ChatResponse> chat(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID conversationId,
            @RequestBody Map<String, String> body) {

        String content = body.get("content");
        if (content == null || content.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // 1. Lưu tin nhắn user — sendMessage tự detect emotion và lưu vào DB
        SendMessageRequest req = new SendMessageRequest();
        req.setRole("user");
        req.setContent(content);
        var userMsg = conversationService.sendMessage(principal.getId(), conversationId, req);

        // 2. Emotion với context inheritance:
        // Nếu turn hiện tại là neutral nhưng các turn trước có emotion nặng → kế thừa
        String emotion = resolveEmotionWithContext(
                userMsg.detectedEmotion(),
                conversationService.getRecentMessages(principal.getId(), conversationId, 6));

        // 3. Chạy RAG pipeline → sinh phản hồi Dora
        ChatResponse rag = ragService.chat(
                principal.getId(), conversationId, content, emotion);

        // 4. Trả về cả tin nhắn user + phản hồi AI (kèm reply/mood/songs — xem RagServiceImpl)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ChatResponse(
                        userMsg, rag.getAiResponse(), rag.getRetrievedSources(),
                        rag.getReply(), rag.getMood(), rag.getSongs()));
    }

    /**
     * Nếu emotion hiện tại là neutral, kiểm tra 3 tin nhắn user gần nhất.
     * Nếu có emotion nặng liên tục → kế thừa để Dora không bị mất context.
     * Crisis luôn được giữ nguyên từ detectEmotion.
     */
    private static final Set<String> STRONG_EMOTIONS = Set.of("anxious", "sad", "angry", "tired", "crisis");

    private String resolveEmotionWithContext(String detected, List<MessageResponse> recentMessages) {
        if (!"neutral".equals(detected)) return detected; // có emotion rõ → dùng luôn

        // Lấy emotion từ 3 tin nhắn user gần nhất (bỏ qua AI messages)
        return recentMessages.stream()
                .filter(m -> "user".equals(m.role()))
                .limit(3)
                .map(MessageResponse::detectedEmotion)
                .filter(e -> e != null && STRONG_EMOTIONS.contains(e))
                .findFirst()         // emotion gần nhất
                .orElse("neutral");  // không có gì → giữ neutral
    }
}
