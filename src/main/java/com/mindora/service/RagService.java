package com.mindora.service;

import com.mindora.dto.response.ChatResponse;
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
}
