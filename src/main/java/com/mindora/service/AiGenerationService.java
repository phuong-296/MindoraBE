package com.mindora.service;

import java.util.List;
import java.util.Map;

public interface AiGenerationService {

    String generateChat(String systemInstruction,
                        List<Map<String, String>> history,
                        String currentUserMessage);

    String generate(String prompt);

    /**
     * Gọi Gemini single-turn với 1 responseSchema TÙY CHỌN (khác CHAT_RESPONSE_SCHEMA cố định
     * của generateChat) — dùng cho các tác vụ phân tích một lần không phải hội thoại, ví dụ
     * DailyInsightService phân tích đoạn chat trong ngày để trả về JSON gamification riêng.
     *
     * @param prompt Nội dung prompt đầy đủ (đã gồm hướng dẫn + dữ liệu cần phân tích)
     * @param schema JSON schema (dạng Map, theo cú pháp Gemini responseSchema) mô tả cấu trúc JSON mong muốn
     * @return Chuỗi JSON đúng theo schema, hoặc null nếu lỗi/không có API key
     */
    String generateJson(String prompt, Map<String, Object> schema);
}
