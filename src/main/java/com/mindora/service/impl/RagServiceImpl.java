package com.mindora.service.impl;

import com.mindora.dto.response.ChatResponse;
import com.mindora.dto.response.MessageResponse;
import com.mindora.entity.AiConversation;
import com.mindora.entity.KnowledgeDocument;
import com.mindora.entity.Message;
import com.mindora.entity.MessageRole;
import com.mindora.exception.ResourceNotFoundException;
import com.mindora.repository.AiConversationRepository;
import com.mindora.repository.KnowledgeDocumentRepository;
import com.mindora.repository.MessageRepository;
import com.mindora.service.RagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * RAG Pipeline cho Dora — AI tư vấn sức khỏe tâm thần của Mindora.
 *
 * Pipeline:
 * 1. RETRIEVE  → Tìm knowledge chunks theo emotion + keyword
 * 2. AUGMENT   → Ghép context vào prompt
 * 3. GENERATE  → Gọi Gemini API
 * 4. FALLBACK  → Template response nếu không có key/lỗi mạng
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RagServiceImpl implements RagService {

    private final KnowledgeDocumentRepository knowledgeRepo;
    private final AiConversationRepository    conversationRepo;
    private final MessageRepository           messageRepo;
    private final GeminiService               geminiService;

    /** Số chunks tối đa đưa vào context */
    private static final int TOP_K = 3;

    /** Số tin nhắn lịch sử đưa vào context */
    private static final int HISTORY_TURNS = 6;

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public ChatResponse chat(UUID userId, UUID conversationId,
                             String userMessage, String detectedEmotion) {

        AiConversation conv = conversationRepo.findById(conversationId)
                .filter(c -> c.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Conversation không tồn tại"));

        // 1. RETRIEVE
        List<KnowledgeDocument> docs = retrieve(detectedEmotion, userMessage);
        log.info("[RAG] Retrieved {} docs for emotion='{}' message='{}'",
                 docs.size(), detectedEmotion, userMessage.substring(0, Math.min(50, userMessage.length())));

        // 2. AUGMENT
        List<MessageResponse> history = getRecentHistory(conversationId);
        String prompt = buildPrompt(userMessage, detectedEmotion, docs, history);

        // 3. GENERATE
        String aiText = geminiService.generate(prompt);

        if (aiText == null || aiText.isBlank()) {
            aiText = buildFallbackResponse(detectedEmotion, docs);
            log.info("[RAG] Dùng fallback response (emotion={})", detectedEmotion);
        }

        // ── 4. LƯU tin nhắn AI vào DB ────────────────────────────────────────
        Message aiMessage = new Message();
        aiMessage.setConversation(conv);
        aiMessage.setRole(MessageRole.AI);
        aiMessage.setContent(aiText.trim());
        messageRepo.save(aiMessage);
        conversationRepo.save(conv); // cập nhật updatedAt

        // ── 5. Build response ─────────────────────────────────────────────────
        List<String> sources = docs.stream()
                .map(d -> d.getSource() + " — " + d.getTitle())
                .distinct()
                .collect(Collectors.toList());

        return new ChatResponse(null, toResponse(aiMessage), sources);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 1: RETRIEVE
    // ─────────────────────────────────────────────────────────────────────────

    private List<KnowledgeDocument> retrieve(String emotion, String message) {
        Set<KnowledgeDocument> result = new LinkedHashSet<>();

        // Ưu tiên 1: emotion tag chính xác
        List<KnowledgeDocument> byEmotion = knowledgeRepo.findByEmotionTag(emotion);
        result.addAll(byEmotion);

        // Ưu tiên 2: keyword từ tin nhắn (top 2 từ)
        if (result.size() < TOP_K) {
            List<String> keywords = extractKeywords(message);
            for (String kw : keywords) {
                knowledgeRepo.findByKeyword(kw).forEach(result::add);
                if (result.size() >= TOP_K * 2) break;
            }
        }

        // Fallback: lấy neutral nếu không có gì
        if (result.isEmpty()) {
            result.addAll(knowledgeRepo.findByEmotionTag("neutral"));
        }

        return result.stream().limit(TOP_K).collect(Collectors.toList());
    }

    /** Trích từ khoá quan trọng từ tin nhắn của user */
    private List<String> extractKeywords(String message) {
        // Stopwords tiếng Việt đơn giản
        Set<String> stopwords = Set.of("tôi", "mình", "bạn", "là", "và", "có", "không",
                "của", "với", "được", "một", "này", "đó", "thì", "để", "từ",
                "trong", "hôm", "nay", "rất", "quá", "cũng", "đang", "hay");

        return Arrays.stream(message.toLowerCase().split("[\\s,\\.!?;:]+"))
                .filter(w -> w.length() > 2 && !stopwords.contains(w))
                .distinct()
                .limit(5)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 2: AUGMENT — Build prompt
    // ─────────────────────────────────────────────────────────────────────────

    private String buildPrompt(String userMessage, String emotion,
                               List<KnowledgeDocument> docs,
                               List<MessageResponse> history) {
        StringBuilder sb = new StringBuilder();

        // System prompt — định hình nhân vật Dora
        sb.append("""
            Bạn là Dora, trợ lý AI tư vấn sức khỏe tâm thần của ứng dụng Mindora.
            Bạn được đào tạo theo chuẩn Sơ cứu Tâm lý (PFA - Psychological First Aid) của WHO.
            
            NGUYÊN TẮC:
            - Lắng nghe empathetically, không phán xét
            - Đặt câu hỏi mở để khuyến khích chia sẻ
            - Cung cấp kỹ thuật thực hành cụ thể (hít thở, mindfulness, nhật ký cảm xúc)
            - KHÔNG đưa ra chẩn đoán y tế. Nếu tình huống nghiêm trọng, khuyến khích gặp chuyên gia
            - Trả lời bằng tiếng Việt, ấm áp và gần gũi
            - Giữ câu trả lời ngắn gọn (3-5 câu), tập trung vào 1 kỹ thuật cụ thể
            
            """);

        // Emotion context
        sb.append("TRẠNG THÁI CẢM XÚC NGƯỜI DÙNG: ").append(translateEmotion(emotion)).append("\n\n");

        // Knowledge context từ RAG
        if (!docs.isEmpty()) {
            sb.append("KIẾN THỨC LIÊN QUAN (từ tài liệu PFA/WHO):\n");
            sb.append("---\n");
            for (int i = 0; i < docs.size(); i++) {
                KnowledgeDocument doc = docs.get(i);
                sb.append("[").append(i + 1).append("] ").append(doc.getTitle()).append("\n");
                sb.append(doc.getContent()).append("\n\n");
            }
            sb.append("---\n\n");
            sb.append("Hãy vận dụng kiến thức trên để hỗ trợ người dùng một cách phù hợp.\n\n");
        }

        // Chat history
        if (!history.isEmpty()) {
            sb.append("LỊCH SỬ CUỘC TRÒ CHUYỆN:\n");
            for (MessageResponse msg : history) {
                String role = msg.role().equals("user") ? "Người dùng" : "Dora";
                sb.append(role).append(": ").append(msg.content()).append("\n");
            }
            sb.append("\n");
        }

        // User message
        sb.append("Người dùng: ").append(userMessage).append("\n");
        sb.append("Dora:");

        return sb.toString();
    }

    private String translateEmotion(String emotion) {
        return switch (emotion) {
            case "anxious"  -> "Lo lắng / Căng thẳng";
            case "sad"      -> "Buồn bã / Thất vọng";
            case "angry"    -> "Tức giận / Bực bội";
            case "tired"    -> "Mệt mỏi / Kiệt sức";
            case "happy"    -> "Vui vẻ / Tích cực";
            default         -> "Trung tính";
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FALLBACK — Khi không có API key hoặc Gemini lỗi
    // ─────────────────────────────────────────────────────────────────────────

    private String buildFallbackResponse(String emotion, List<KnowledgeDocument> docs) {
        // Nếu có docs, dùng nội dung doc đầu tiên làm response
        if (!docs.isEmpty()) {
            KnowledgeDocument best = docs.get(0);
            String tip = best.getContent().length() > 300
                    ? best.getContent().substring(0, 300) + "..."
                    : best.getContent();
            return switch (emotion) {
                case "anxious"  -> "Mình hiểu bạn đang lo lắng. " + tip;
                case "sad"      -> "Mình ở đây để lắng nghe bạn. " + tip;
                case "angry"    -> "Cảm xúc của bạn hoàn toàn có lý. " + tip;
                case "tired"    -> "Bạn cần được nghỉ ngơi và chăm sóc bản thân. " + tip;
                default         -> "Cảm ơn bạn đã chia sẻ với Dora. " + tip;
            };
        }

        // Default fallback
        return switch (emotion) {
            case "anxious"  -> "Mình hiểu bạn đang cảm thấy lo lắng. Hãy thử kỹ thuật hít thở 4-7-8: hít vào 4 giây, nín thở 7 giây, thở ra 8 giây. Làm 3-4 lần sẽ giúp hệ thần kinh bình tĩnh lại. Bạn có muốn thử ngay bây giờ không?";
            case "sad"      -> "Mình ở đây lắng nghe bạn. Cảm giác buồn là bình thường và bạn không cần phải một mình vượt qua điều này. Bạn có thể kể thêm cho Dora nghe chuyện gì đang xảy ra không?";
            case "angry"    -> "Mình hiểu bạn đang rất bực bội. Hãy cho bản thân 1 phút: đứng dậy, đi bộ chậm hoặc rửa mặt bằng nước mát. Khi cơ thể bình tĩnh hơn, mình sẽ cùng bạn giải quyết vấn đề nhé.";
            case "tired"    -> "Bạn đang mang quá nhiều gánh nặng rồi. Hôm nay bạn đã làm được điều gì nhỏ thôi cũng được — đó đã là thành công. Bạn cần nghỉ ngơi thật sự, không chỉ về thể xác mà cả tinh thần.";
            default         -> "Cảm ơn bạn đã chia sẻ cảm xúc của mình với Dora. Mình ở đây để lắng nghe. Bạn có muốn kể thêm không?";
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private List<MessageResponse> getRecentHistory(UUID conversationId) {
        return messageRepo
                .findByConversationIdOrderByCreatedAtAsc(
                        conversationId,
                        PageRequest.of(0, HISTORY_TURNS))
                .getContent()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private MessageResponse toResponse(Message m) {
        return new MessageResponse(
                m.getId(),
                m.getRole().name().toLowerCase(),
                m.getContent(),
                m.getDetectedEmotion(),
                m.getSentimentScore() != null ? m.getSentimentScore() : BigDecimal.ZERO,
                m.getCreatedAt());
    }
}
