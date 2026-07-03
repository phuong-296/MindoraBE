package com.mindora.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Client gọi Gemini API.
 *
 * Hỗ trợ hai chế độ:
 * - generateChat: multi-turn chat với systemInstruction (dùng cho RAG pipeline)
 * - generate:     single-turn prompt thuần (backward compat)
 */
@Slf4j
@Service
public class GeminiService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent}")
    private String apiUrl;

    @Value("${gemini.max.tokens:800}")
    private int maxTokens;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Gọi Gemini với định dạng multi-turn chat — đây là cách đúng để gửi lịch sử hội thoại.
     *
     * @param systemInstruction  Hướng dẫn nhân vật Dora + knowledge context
     * @param history            Lịch sử hội thoại: mỗi phần tử là Map {"role": "user"|"model", "content": "..."}
     * @param currentUserMessage Tin nhắn hiện tại của user
     * @return Nội dung phản hồi của AI, hoặc null nếu lỗi
     */
    public String generateChat(String systemInstruction,
                               List<Map<String, String>> history,
                               String currentUserMessage) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("[Gemini] API key chưa cấu hình — dùng fallback response");
            return null;
        }

        try {
            String url = apiUrl + "?key=" + apiKey;

            // Xây dựng contents: lịch sử + tin nhắn hiện tại
            List<Map<String, Object>> contents = new ArrayList<>();
            for (Map<String, String> turn : history) {
                contents.add(Map.of(
                    "role",  turn.get("role"),
                    "parts", List.of(Map.of("text", turn.get("content")))
                ));
            }
            contents.add(Map.of(
                "role",  "user",
                "parts", List.of(Map.of("text", currentUserMessage))
            ));

            Map<String, Object> body = Map.of(
                "system_instruction", Map.of(
                    "parts", List.of(Map.of("text", systemInstruction))
                ),
                "contents", contents,
                "generationConfig", Map.of(
                    "maxOutputTokens", maxTokens,
                    "temperature",     0.90,
                    "topP",            0.95
                ),
                "safetySettings", List.of(
                    Map.of("category", "HARM_CATEGORY_HARASSMENT",        "threshold", "BLOCK_NONE"),
                    Map.of("category", "HARM_CATEGORY_HATE_SPEECH",       "threshold", "BLOCK_NONE"),
                    Map.of("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT", "threshold", "BLOCK_NONE"),
                    Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold", "BLOCK_NONE")
                )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return extractText(response.getBody());
            }

        } catch (Exception e) {
            log.error("[Gemini] Lỗi khi gọi API: {}", e.getMessage());
        }

        return null;
    }

    /** Single-turn prompt — dùng khi không cần lịch sử hội thoại */
    public String generate(String prompt) {
        return generateChat("", List.of(), prompt);
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> responseBody) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                if (parts != null && !parts.isEmpty()) {
                    return (String) parts.get(0).get("text");
                }
            }
        } catch (Exception e) {
            log.error("[Gemini] Lỗi parse response: {}", e.getMessage());
        }
        return null;
    }
}
