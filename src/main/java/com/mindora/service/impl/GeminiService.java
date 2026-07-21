package com.mindora.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.mindora.service.AiGenerationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GeminiService implements AiGenerationService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent}")
    private String apiUrl;

    @Value("${gemini.max.tokens:1024}")
    private int maxTokens;

    /**
     * QUAN TRỌNG: RestTemplate() mặc định KHÔNG có timeout (connect/read = vô hạn).
     * Nếu mạng chập chờn hoặc Google chậm phản hồi, request có thể treo rất lâu (nhiều phút)
     * trước khi lỗi — đây là nguyên nhân phổ biến khiến chat "rất lag". Set timeout rõ ràng
     * để request luôn thất bại nhanh và rơi về fallback response thay vì treo UI.
     */
    private static final int CONNECT_TIMEOUT_MS = 5_000;
    private static final int READ_TIMEOUT_MS    = 20_000;

    private final RestTemplate restTemplate = buildRestTemplate();

    private static RestTemplate buildRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT_MS);
        factory.setReadTimeout(READ_TIMEOUT_MS);
        return new RestTemplate(factory);
    }

    /**
     * Schema bắt buộc Gemini trả lời đúng dạng JSON {"reply", "mood", "suggestMusic"} thay vì text tự do.
     * Dùng tính năng "structured output" chính thức của Gemini (responseMimeType + responseSchema) nên
     * không cần tự regex/đoán JSON từ text — Gemini luôn trả đúng cấu trúc và đúng 1 trong 6 giá trị mood.
     * mood chỉ dùng để backend tự truy vấn bài hát trong DB — Gemini không tự đề xuất bài hát/link nào.
     * suggestMusic: Gemini tự quyết định lượt chat NÀY có nên đính kèm nhạc hay không — tránh việc
     * câu nào cũng bị gắn bài hát dù người dùng không cần, gây rối và mất tự nhiên trong hội thoại.
     */
    private static final Map<String, Object> CHAT_RESPONSE_SCHEMA = Map.of(
        "type", "OBJECT",
        "properties", Map.of(
            "reply", Map.of(
                "type", "STRING",
                "description", "Câu trả lời của Dora dành cho người dùng, thuần văn bản, không chèn link hay markdown."
            ),
            "mood", Map.of(
                "type", "STRING",
                "enum", List.of("happy", "calm", "sad", "stress", "sleep", "energy"),
                "description", "Tâm trạng của người dùng được suy ra từ TOÀN BỘ nội dung cuộc trò chuyện."
            ),
            "suggestMusic", Map.of(
                "type", "BOOLEAN",
                "description", "true CHỈ khi lượt chat này nên gợi ý nhạc (người dùng xin nhạc, hoặc " +
                    "đang cảm xúc mạnh và một bài nhạc phù hợp sẽ thực sự hữu ích ngay lúc này); " +
                    "false cho hầu hết các lượt trò chuyện bình thường khác."
            )
        ),
        "required", List.of("reply", "mood", "suggestMusic")
    );

    /**
     * Gọi Gemini với định dạng multi-turn chat — đây là cách đúng để gửi lịch sử hội thoại.
     * Luôn yêu cầu Gemini trả JSON có cấu trúc {"reply", "mood", "suggestMusic"} (xem CHAT_RESPONSE_SCHEMA).
     *
     * @param systemInstruction  Hướng dẫn nhân vật Dora + knowledge context
     * @param history            Lịch sử hội thoại: mỗi phần tử là Map {"role": "user"|"model", "content": "..."}
     * @param currentUserMessage Tin nhắn hiện tại của user
     * @return Chuỗi JSON {"reply": "...", "mood": "...", "suggestMusic": true|false}, hoặc null nếu lỗi
     */
    public String generateChat(String systemInstruction,
                               List<Map<String, String>> history,
                               String currentUserMessage) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("[Gemini] API key chưa cấu hình — dùng fallback response");
            return null;
        }

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
                "maxOutputTokens",   maxTokens,
                "temperature",       0.85,
                "topP",              0.95,
                "topK",              40,
                "thinkingConfig",    Map.of("thinkingBudget", 0),
                "responseMimeType",  "application/json",
                "responseSchema",    CHAT_RESPONSE_SCHEMA
            ),
            "safetySettings", SAFETY_SETTINGS
        );

        return executeRequest(body);
    }

    /**
     * Single-turn prompt — dùng khi không cần lịch sử hội thoại.
     * Lưu ý: cũng trả JSON {"reply","mood","suggestMusic"} vì delegate qua generateChat().
     */
    public String generate(String prompt) {
        return generateChat("", List.of(), prompt);
    }

    /**
     * Single-turn, KHÔNG dùng CHAT_RESPONSE_SCHEMA cố định — nhận 1 schema tùy ý cho từng tác vụ
     * phân tích riêng (vd DailyInsightService). Không có system_instruction/history vì đây là
     * request 1 lần, không phải hội thoại — toàn bộ ngữ cảnh cần thiết phải nằm trong `prompt`.
     */
    @Override
    public String generateJson(String prompt, Map<String, Object> schema) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("[Gemini] API key chưa cấu hình — bỏ qua generateJson");
            return null;
        }

        Map<String, Object> body = Map.of(
            "contents", List.of(Map.of(
                "role",  "user",
                "parts", List.of(Map.of("text", prompt))
            )),
            "generationConfig", Map.of(
                "maxOutputTokens",   maxTokens,
                "temperature",       0.7,
                "topP",              0.95,
                "topK",              40,
                "thinkingConfig",    Map.of("thinkingBudget", 0),
                "responseMimeType",  "application/json",
                "responseSchema",    schema
            ),
            "safetySettings", SAFETY_SETTINGS
        );

        return executeRequest(body);
    }

    /** Cấu hình an toàn dùng chung — nới lỏng filter mặc định vì nội dung tâm sự có thể nhạy cảm. */
    private static final List<Map<String, String>> SAFETY_SETTINGS = List.of(
        Map.of("category", "HARM_CATEGORY_HARASSMENT",        "threshold", "BLOCK_NONE"),
        Map.of("category", "HARM_CATEGORY_HATE_SPEECH",       "threshold", "BLOCK_NONE"),
        Map.of("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT", "threshold", "BLOCK_NONE"),
        Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold", "BLOCK_NONE")
    );

    /**
     * Gửi request tới Gemini + retry/backoff cho 429 + xử lý timeout — logic dùng chung cho mọi
     * loại request (chat nhiều lượt hay phân tích 1 lần), tách ra để không lặp code giữa
     * generateChat() và generateJson().
     */
    private String executeRequest(Map<String, Object> body) {
        int maxRetries = 2;
        int[] backoffSeconds = {15, 30};

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                String url = apiUrl + "?key=" + apiKey;

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

                ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    return extractText(response.getBody());
                }

            } catch (HttpClientErrorException e) {
                if (e.getStatusCode().value() == 429 && attempt < maxRetries) {
                    int wait = backoffSeconds[attempt];
                    log.warn("[Gemini] Rate limit 429 — thử lại sau {}s (lần {}/{})", wait, attempt + 1, maxRetries);
                    try { Thread.sleep(wait * 1000L); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
                } else {
                    log.error("[Gemini] Lỗi HTTP {}: {}", e.getStatusCode().value(), e.getMessage());
                    break;
                }
            } catch (ResourceAccessException e) {
                // Timeout (connect/read) hoặc lỗi mạng (DNS, không có internet, firewall chặn...).
                // Không retry — rớt thẳng về fallback response để user không phải đợi lâu.
                log.error("[Gemini] Timeout/lỗi mạng khi gọi API ({}ms connect / {}ms read): {}",
                        CONNECT_TIMEOUT_MS, READ_TIMEOUT_MS, e.getMessage());
                break;
            } catch (Exception e) {
                log.error("[Gemini] Lỗi khi gọi API: {}", e.getMessage());
                break;
            }
        }

        return null;
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
