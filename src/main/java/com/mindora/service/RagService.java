package com.mindora.service;

import com.mindora.dto.response.ChatResponse;
import com.mindora.dto.response.GuestChatResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RagService {
    /**
     * Full RAG pipeline:
     * 1. Retrieve relevant knowledge chunks
     * 2. Build augmented prompt
     * 3. Generate response via Gemini
     * 4. Save AI message to DB
     */
    ChatResponse chat(UUID userId, UUID conversationId, String userMessage, String detectedEmotion);

    /**
     * Chat với Dora khi CHƯA đăng nhập — cùng pipeline Retrieve/Augment/Generate/Fallback và cùng
     * lưới an toàn crisis, nhưng KHÔNG lưu bất kỳ gì vào DB (không có conversation/message).
     * Lịch sử hội thoại do client tự gửi lên mỗi lần gọi (chỉ tồn tại tạm trong phiên trình duyệt).
     */
    GuestChatResponse chatGuest(String userMessage, String detectedEmotion, List<Map<String, String>> history);
}
