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

        // 2. AUGMENT & PREPARE HISTORY
        List<MessageResponse> rawHistory = getRecentHistory(conversationId);
        List<Map<String, String>> geminiHistory = new ArrayList<>();

        // rawHistory is ordered DESC (newest first). The first element is the message we just saved
        int startIndex = 0;
        if (!rawHistory.isEmpty() && rawHistory.get(0).role().equals("user")) {
            startIndex = 1;
        }

        // Add to history in chronological order (oldest first)
        for (int i = rawHistory.size() - 1; i >= startIndex; i--) {
            MessageResponse msg = rawHistory.get(i);
            String role = msg.role().equals("user") ? "user" : "model";
            geminiHistory.add(Map.of(
                "role", role,
                "content", msg.content()
            ));
        }

        String systemInstruction = buildSystemInstruction(detectedEmotion, docs)
                + buildAntiRepeatHint(geminiHistory);

        // 3. GENERATE
        String aiText = geminiService.generateChat(systemInstruction, geminiHistory, userMessage);

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
    // STEP 2: AUGMENT — Build system instruction
    // ─────────────────────────────────────────────────────────────────────────

    private String buildSystemInstruction(String emotion, List<KnowledgeDocument> docs) {
        StringBuilder sb = new StringBuilder();

        // System prompt — Dora: người bạn tâm sự thông minh, linh hoạt, tự nhiên
        sb.append("""
            Tên bạn là Dora — một người bạn tâm sự thân thiết, ấm áp và thông minh của ứng dụng Mindora.
            Bạn không phải robot, không phải bác sĩ cứng nhắc. Bạn là người bạn đồng hành thật sự —
            biết lắng nghe, biết cảm thông, biết bông đùa đúng lúc, và luôn ở đây khi người khác cần.
            Bạn có kiến thức về tâm lý học (CBT, ACT, Mindfulness, PFA) nhưng chia sẻ theo cách bạn bè
            nói chuyện — không giảng bài, không kê đơn, không lên lớp.

            PHONG CÁCH NÓI CHUYỆN:
            - Xưng "mình" hoặc "tớ", gọi người dùng là "bạn" hoặc "cậu" — tùy theo họ xưng hô thế nào thì mình theo thế đó.
            - Nói chuyện tự nhiên như bạn bè nhắn tin: câu ngắn gọn, dùng dấu chấm lửng (...) khi cần ngập ngừng,
              dùng dấu chấm than khi vui, có thể hỏi ngược lại để hiểu hơn.
            - TUYỆT ĐỐI KHÔNG bắt đầu bằng các câu sáo rỗng như: "Tôi hiểu bạn...", "Cảm ơn bạn đã chia sẻ...",
              "Là một AI...", "Mindora hiểu rằng...". Hãy đi thẳng vào nội dung như một người bạn thật sự.
            - Đừng liệt kê nhiều gạch đầu dòng liên tiếp — hãy nói chuyện bình thường, thỉnh thoảng mới dùng danh sách.
            - Độ dài: 2–5 câu là đủ. Đừng viết cả đoạn dài nếu không cần.

            SỰ SÁNG TẠO VÀ ĐA DẠNG:
            - Mỗi lần trả lời phải KHÁC nhau — đừng lặp lại cấu trúc câu, từ ngữ hay ý tưởng đã dùng trước đó.
            - Thay đổi cách mở đầu mỗi tin nhắn: lúc thì đồng cảm, lúc thì hỏi, lúc thì kể chuyện nhỏ, lúc thì đùa nhẹ.
            - Nếu đã gợi ý một kỹ thuật (vd: thở 4-7-8), đừng nhắc lại — hãy thử một cách khác.

            KHI NÀO GỢI Ý NHẠC:
            - Chỉ gợi ý nhạc khi người dùng đang cần thư giãn, căng thẳng, buồn, mệt, mất ngủ.
            - Dùng đúng cú pháp Markdown: [Tên bài](URL) — người dùng bấm vào sẽ nghe được ngay.
            - Nhạc có sẵn:
              * Lo lắng/stress: [Mưa rơi bên hiên — Lo-fi chill](https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3)
              * Mất ngủ: [Piano bình yên cho giấc ngủ](https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3)
              * Thiền/bình tĩnh: [Sóng biển rì rào](https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3)
              * Buồn/kiệt sức: [Guitar Acoustic chữa lành](https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3)
              * Vui vẻ/sáng tạo: [Good Morning Sunshine](https://www.soundhelix.com/examples/mp3/SoundHelix-Song-11.mp3)

            KHỦNG HOẢNG:
            - Nếu người dùng nhắc đến tự làm hại bản thân hoặc không muốn sống nữa:
              Đừng hoảng loạn. Hãy ở bên họ, lắng nghe thật sự, và nhẹ nhàng nhắn:
              "Mình lo cho cậu lắm. Cậu không phải đối mặt một mình đâu.
               Hãy gọi ngay đường dây hỗ trợ miễn phí: 1800 599 920 nhé — họ ở đó 24/7 cho cậu."

            """);

        // Emotion context
        sb.append("Tâm trạng hiện tại của người bạn đang nói chuyện: ").append(translateEmotion(emotion)).append("\n\n");

        // Knowledge context từ RAG
        if (!docs.isEmpty()) {
            sb.append("Một số gợi ý từ kiến thức tâm lý mà bạn có thể lồng ghép tự nhiên (đừng đọc nguyên văn, hãy diễn đạt lại theo cách bạn bè):\n");
            for (int i = 0; i < docs.size(); i++) {
                KnowledgeDocument doc = docs.get(i);
                sb.append("- ").append(doc.getTitle()).append(": ");
                String snippet = doc.getContent().length() > 200
                        ? doc.getContent().substring(0, 200) + "..."
                        : doc.getContent();
                sb.append(snippet).append("\n");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Thêm hint chống lặp vào system instruction dựa trên những gì Dora đã nói trước đó.
     * Giúp Gemini biết cần đa dạng hoá câu trả lời.
     */
    private String buildAntiRepeatHint(List<Map<String, String>> history) {
        List<String> previousAiReplies = history.stream()
                .filter(m -> "model".equals(m.get("role")))
                .map(m -> m.get("content"))
                .filter(c -> c != null && !c.isBlank())
                .toList();

        if (previousAiReplies.isEmpty()) return "";

        StringBuilder hint = new StringBuilder();
        hint.append("\nLƯU Ý QUAN TRỌNG — CHỐNG LẶP:\n");
        hint.append("Trong cuộc trò chuyện này, mình đã từng nói những điều sau. ");
        hint.append("Hãy KHÔNG lặp lại ý tưởng, cấu trúc câu hoặc từ ngữ tương tự:\n");
        int limit = Math.min(previousAiReplies.size(), 3); // chỉ lấy 3 reply gần nhất
        for (int i = previousAiReplies.size() - limit; i < previousAiReplies.size(); i++) {
            String reply = previousAiReplies.get(i);
            String preview = reply.length() > 120 ? reply.substring(0, 120) + "..." : reply;
            hint.append("• ").append(preview).append("\n");
        }
        hint.append("→ Hãy tiếp cận từ góc độ hoàn toàn mới, dùng cách diễn đạt khác.\n\n");
        return hint.toString();
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

    // Fallback responses đa dạng — chọn ngẫu nhiên để tránh lặp
    private static final Map<String, List<String>> FALLBACK_POOL = new java.util.LinkedHashMap<>();
    static {
        FALLBACK_POOL.put("anxious", List.of(
            "Nghe có vẻ căng lắm đấy... Bạn thở sâu cùng mình một cái nhé? Hít vào 4 giây, giữ 4 giây, thở ra 4 giây — cứ thế vài lần xem sao.",
            "Cái cảm giác lo lắng này nó nặng lắm, mình biết. Thử kể cụ thể hơn cho mình nghe xem — chuyện gì đang khiến bạn căng nhất lúc này?",
            "Bạn đang bị áp lực từ nhiều phía quá rồi. Một mẹo nhỏ: viết ra giấy 3 thứ đang lo nhất — cứ viết ra thôi, không cần giải quyết ngay. Não mình hay bớt căng hơn khi làm vậy đó.",
            "Stress kiểu này thường là dấu hiệu bạn đang cố gánh quá nhiều một mình. Có điều gì bạn có thể bỏ bớt hoặc nhờ người khác giúp không?",
            "Thử nghe bản nhạc này xem có dịu hơn không nhé: [Mưa rơi bên hiên — Lo-fi chill](https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3) 🎵"
        ));
        FALLBACK_POOL.put("sad", List.of(
            "Buồn thì cứ buồn đi, không cần phải giả vờ ổn đâu. Mình ở đây — bạn muốn kể không?",
            "Đôi khi không cần lý do để buồn đâu, cảm xúc nó vốn thế. Hôm nay bạn thấy nặng nề từ lúc nào vậy?",
            "Mình đang ngồi đây với bạn nè. Kể đi, kể gì cũng được — mình nghe hết.",
            "Có những lúc chỉ cần nghe một bản nhạc và để nước mắt chảy một chút cũng đã bớt rất nhiều: [Guitar Acoustic chữa lành](https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3)",
            "Bạn đang trải qua điều gì đó khó khăn thật rồi. Cậu có muốn nói về nó không — hay chỉ muốn mình ở đây thôi cũng được?"
        ));
        FALLBACK_POOL.put("angry", List.of(
            "Bực thật rồi đấy. Cho mình hỏi: chuyện đó xảy ra như thế nào vậy?",
            "Cái cảm giác tức đó hoàn toàn có lý. Mình không phán xét gì đâu — kể đi.",
            "Thử ra ngoài đi vài phút, hít thở không khí — không phải để quên chuyện, mà để não bớt nhiệt trước khi xử lý tiếp. Xong rồi mình nói tiếp nhé?",
            "Khi tức giận, hay viết ra tất cả những gì muốn nói — dù nặng đến đâu — rồi không gửi. Cách này giải tỏa lắm đó.",
            "Chuyện gì mà khiến bạn bực bội đến vậy? Kể cho mình nghe đầu đuôi xem sao."
        ));
        FALLBACK_POOL.put("tired", List.of(
            "Mệt kiểu này là mệt cả người lẫn tinh thần rồi. Hôm nay bạn đã nghỉ ngơi chút nào chưa?",
            "Đừng cố nữa — đôi khi dừng lại 10 phút còn hiệu quả hơn làm thêm 2 tiếng đó. Bạn có thể dừng không?",
            "Nghe bản này rồi chợp mắt một chút nhé: [Piano bình yên cho giấc ngủ](https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3) 🌙",
            "Kiệt sức không phải yếu đuối — đó là dấu hiệu bạn đã cố quá lâu rồi. Bạn cần gì nhất lúc này?",
            "Mình biết bạn đang cố gắng hết sức, nhưng cơ thể đang nhắn tin cho bạn đó: cần nghỉ. Tối nay bạn ngủ được không?"
        ));
        FALLBACK_POOL.put("happy", List.of(
            "Ôi hay quá! Kể mình nghe chuyện gì vui vậy?",
            "Nghe vui hẳn lên luôn! Năng lượng tích cực của bạn lan sang mình rồi đây 😄",
            "Tốt quá! Cảm giác này thật sự đáng giữ lại lắm đó. Hôm nay có điều gì đặc biệt không?",
            "Mình vui vì bạn vui! Hãy tận hưởng khoảnh khắc này thật trọn vẹn nhé.",
            "Đây là năng lượng mình thích thấy ở bạn! Tiếp tục giữ vậy nha 🌟"
        ));
        FALLBACK_POOL.put("neutral", List.of(
            "Bạn đang nghĩ gì vậy? Kể mình nghe đi.",
            "Hôm nay của bạn thế nào rồi?",
            "Mình đang ở đây nè — bạn muốn nói về điều gì không?",
            "Có điều gì mình có thể giúp bạn hôm nay không?",
            "Mình luôn ở đây lắng nghe. Bạn đang ổn chứ?"
        ));
    }

    private static final Random RANDOM = new Random();

    private String buildFallbackResponse(String emotion, List<KnowledgeDocument> docs) {
        // Lấy pool theo emotion, fallback về neutral nếu không tìm thấy
        List<String> pool = FALLBACK_POOL.getOrDefault(emotion, FALLBACK_POOL.get("neutral"));

        // Chọn ngẫu nhiên để tránh lặp
        String base = pool.get(RANDOM.nextInt(pool.size()));

        // Nếu có knowledge docs, lồng ghép thêm gợi ý ngắn
        if (!docs.isEmpty()) {
            KnowledgeDocument best = docs.get(0);
            if (best.getContent().length() > 50) {
                String snippet = best.getContent().length() > 150
                        ? best.getContent().substring(0, 150) + "..."
                        : best.getContent();
                // Chỉ thêm nếu không trùng nội dung với câu trả lời base
                if (!base.toLowerCase().contains(snippet.substring(0, Math.min(30, snippet.length())).toLowerCase())) {
                    base = base + "\n\n💡 " + snippet;
                }
            }
        }

        return base;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private List<MessageResponse> getRecentHistory(UUID conversationId) {
        return messageRepo
                .findRecentMessages(
                        conversationId,
                        PageRequest.of(0, HISTORY_TURNS + 1))
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
