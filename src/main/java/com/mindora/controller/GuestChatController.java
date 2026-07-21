package com.mindora.controller;

import com.mindora.dto.response.GuestChatResponse;
import com.mindora.service.EmotionAnalysisService;
import com.mindora.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Chat với Dora khi CHƯA đăng nhập — cho phép người dùng mới trải nghiệm Dora trước khi quyết
 * định đăng ký tài khoản. KHÔNG tạo AiConversation/Message nào trong DB — toàn bộ lịch sử chỉ
 * tồn tại tạm ở phía client (mất khi tải lại trang), khớp đúng nguyên tắc "chat được nhưng
 * không lưu lịch sử trò chuyện" khi chưa đăng nhập.
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class GuestChatController {

    private final RagService ragService;
    private final EmotionAnalysisService emotionService;

    private static final Set<String> STRONG_EMOTIONS = Set.of("anxious", "sad", "angry", "tired", "crisis");

    /**
     * Body: { "message": "...", "history": [{"role":"user"|"ai","content":"..."}, ...] }
     * history do client tự quản lý (không có ở DB) — dùng để Dora hiểu ngữ cảnh nhiều lượt.
     */
    @PostMapping("/guest")
    public ResponseEntity<GuestChatResponse> guestChat(@RequestBody Map<String, Object> body) {
        Object messageObj = body.get("message");
        String message = messageObj instanceof String s ? s : null;
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        @SuppressWarnings("unchecked")
        List<Map<String, String>> history = body.get("history") instanceof List<?> list
                ? (List<Map<String, String>>) list
                : List.of();

        String emotion = resolveEmotionWithContext(message, history);
        GuestChatResponse response = ragService.chatGuest(message, emotion, history);
        return ResponseEntity.ok(response);
    }

    /**
     * Guest không có gì lưu sẵn trong DB nên tự suy luận lại emotion từ vài tin nhắn user gần
     * nhất trong lịch sử client gửi lên — cùng nguyên tắc kế thừa emotion với ConversationController
     * (nếu lượt hiện tại trung tính nhưng vài lượt trước có cảm xúc nặng, vẫn giữ ngữ cảnh đó).
     */
    private String resolveEmotionWithContext(String currentMessage, List<Map<String, String>> history) {
        String detected = emotionService.detectEmotion(currentMessage);
        if (!"neutral".equals(detected)) return detected;

        List<String> recentUserTexts = history.stream()
                .filter(h -> "user".equals(h.get("role")))
                .map(h -> h.get("content"))
                .filter(c -> c != null && !c.isBlank())
                .toList();

        int checked = 0;
        for (int i = recentUserTexts.size() - 1; i >= 0 && checked < 3; i--, checked++) {
            String pastEmotion = emotionService.detectEmotion(recentUserTexts.get(i));
            if (STRONG_EMOTIONS.contains(pastEmotion)) return pastEmotion;
        }
        return "neutral";
    }
}
