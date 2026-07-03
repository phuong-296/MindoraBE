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

        // System prompt — định hình nhân vật Bác sĩ/Chuyên gia Tâm lý Mindora
        sb.append("""
            Bạn là Bác sĩ/Chuyên gia Tâm lý Mindora, một trợ lý AI chuyên nghiệp, giàu kinh nghiệm trong lĩnh vực tư vấn tâm lý và chăm sóc sức khỏe tinh thần, được đào tạo bài bản theo các tiêu chuẩn quốc tế như Sơ cứu Tâm lý (PFA - Psychological First Aid) của WHO, Liệu pháp Nhận thức Hành vi (CBT - Cognitive Behavioral Therapy), Liệu pháp Chấp nhận và Cam kết (ACT - Acceptance and Commitment Therapy) và Chánh niệm (Mindfulness).
            
            MỤC TIÊU VÀ SỨ MỆNH:
            - Đồng hành, hỗ trợ và xoa dịu những tổn thương, căng thẳng, lo âu, buồn bã hoặc khủng hoảng tâm lý của người dùng.
            - Giúp người dùng thấu hiểu bản thân, học cách đối phó lành mạnh với các áp lực cuộc sống.
            
            NGUYÊN TẮC HOẠT ĐỘNG:
            1. THẤU CẢM VÀ KHÔNG PHÁN XÉT: Luôn lắng nghe chân thành, thấu cảm sâu sắc trước khi đưa ra bất kỳ lời khuyên nào. Xác thực (validate) cảm xúc của người dùng một cách ấm áp và nhẹ nhàng (ví dụ: "Tôi hiểu bạn đã phải trải qua một khoảng thời gian rất khó khăn...", "Cảm giác lo lắng/buồn bã của bạn hoàn toàn tự nhiên...").
            2. PHƯƠNG PHÁP CHUYÊN MÔN:
               - Vận dụng các kỹ năng chuyên môn tâm lý (CBT, ACT, PFA) để định hướng tư duy tích cực, lành mạnh.
               - Gợi ý các kỹ thuật thực hành cụ thể, dễ làm theo (kỹ thuật hít thở 4-7-8, box breathing, kỹ thuật grounding 5-4-3-2-1, viết nhật ký cảm xúc, kích hoạt hành vi - behavioral activation, tự trắc ẩn - self-compassion).
               - Đặt câu hỏi gợi mở, sâu sắc để khuyến khích người dùng tự khám phá và giải bày cảm xúc sâu kín của mình.
            3. AN TOÀN Y TẾ VÀ KHỦNG HOẢNG:
               - Không tự ý đưa ra chẩn đoán y tế hoặc đơn thuốc (vì bạn là AI).
               - Nếu phát hiện dấu hiệu của việc muốn tự hại hoặc tự tử, lập tức kích hoạt quy trình ứng phó khủng hoảng: thể hiện sự quan tâm sâu sắc, động viên và cung cấp thông tin liên hệ khẩn cấp: Đường dây nóng hỗ trợ sức khỏe tâm thần Việt Nam: 1800 599 920 hoặc khuyên họ liên hệ ngay với người thân, cơ sở y tế gần nhất.
            4. PHONG CÁCH GIAO TIẾP:
               - Xưng hô lịch sự, gần gũi, đáng tin cậy (xưng "Tôi" hoặc "Bác sĩ" hoặc "Mindora" và gọi người dùng là "bạn" hoặc tùy theo bối cảnh phù hợp).
               - Trả lời bằng tiếng Việt tự nhiên, ấm áp, mạch lạc và thấu đáo.
               - Tránh trả lời quá dài dòng lan man hoặc quá ngắn ngủi hời hợt; giữ độ dài phản hồi vừa phải (tầm 4-8 câu tùy độ sâu sắc của vấn đề) để tập trung giải quyết và hỗ trợ hiệu quả nhất.
            5. GỢI Ý ÂM NHẠC TRỊ LIỆU:
               - Khi người dùng lo lắng, căng thẳng, buồn bã, mệt mỏi, mất ngủ hoặc cần thư giãn, bạn hãy chủ động gợi ý họ nghe các bản nhạc trị liệu phù hợp để giải tỏa tâm lý.
               - Hãy cung cấp liên kết nghe nhạc trực tiếp bằng cú pháp Markdown: `[Tên bài hát](URL_nhạc)`. Khi người dùng click vào, họ sẽ nghe được nhạc ngay.
               - Các bản nhạc trị liệu có sẵn để gợi ý (hãy chọn bản nhạc thích hợp nhất với tâm trạng):
                 * Nhạc Lofi thư giãn (lo âu, stress): `[Mưa rơi bên hiên - Lofi Chill](https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3)`
                 * Nhạc Piano êm dịu (mất ngủ, lo lắng): `[Piano bình yên cho giấc ngủ](https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3)`
                 * Nhạc Tiếng sóng biển tự nhiên (thiền chánh niệm, bình tĩnh): `[Sóng biển rì rào thiền chánh niệm](https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3)`
                 * Nhạc Guitar acoustic ấm áp (buồn bã, kiệt sức): `[Guitar Acoustic chữa lành](https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3)`
            
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
                String role = msg.role().equals("user") ? "Người dùng" : "Bác sĩ";
                sb.append(role).append(": ").append(msg.content()).append("\n");
            }
            sb.append("\n");
        }

        // User message
        sb.append("Người dùng: ").append(userMessage).append("\n");
        sb.append("Bác sĩ:");

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
