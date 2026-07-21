package com.mindora.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindora.dto.response.ChatResponse;
import com.mindora.dto.response.GuestChatResponse;
import com.mindora.dto.response.MessageResponse;
import com.mindora.dto.response.SongResponse;
import com.mindora.entity.AiConversation;
import com.mindora.entity.ContentLibrary;
import com.mindora.entity.KnowledgeDocument;
import com.mindora.entity.Message;
import com.mindora.entity.MessageRole;
import com.mindora.exception.ResourceNotFoundException;
import com.mindora.repository.AiConversationRepository;
import com.mindora.repository.ContentLibraryRepository;
import com.mindora.repository.KnowledgeDocumentRepository;
import com.mindora.repository.MessageRepository;
import com.mindora.service.AiGenerationService;
import com.mindora.service.RagService;
import com.mindora.util.SongMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
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
    private final AiGenerationService         aiGenerationService;
    private final ContentLibraryRepository    contentRepo;
    private final ObjectMapper                objectMapper;

    /** Số chunks tối đa đưa vào context */
    private static final int TOP_K = 5;

    /** Số tin nhắn lịch sử đưa vào context (5-10 tin gần nhất để Gemini hiểu ngữ cảnh) */
    private static final int HISTORY_TURNS = 10;

    /** 6 giá trị mood hợp lệ duy nhất — khớp đúng yêu cầu, dùng để validate output của Gemini. */
    private static final Set<String> VALID_MOODS =
            Set.of("happy", "calm", "sad", "stress", "sleep", "energy");

    /**
     * JSON structured output Gemini phải trả: {"reply": "...", "mood": "...", "suggestMusic": bool}.
     * suggestMusic do chính Gemini quyết định — chỉ true khi lượt chat NÀY thực sự nên kèm nhạc
     * (người dùng xin nhạc, hoặc cảm xúc mạnh mà một bài hát sẽ hữu ích ngay lúc này), tránh việc
     * câu nào cũng bị gắn bài hát dù không liên quan, gây rối luồng trò chuyện.
     */
    private record GeminiChatOutput(String reply, String mood, Boolean suggestMusic) {}

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

        // CRISIS: uu tien tuyet doi - luon tra ve hotline, KHONG goi Gemini.
        // Đây là lưới an toàn riêng, cố tình KHÔNG phụ thuộc vào mood do Gemini trả về
        // (mood 6 giá trị happy/calm/sad/stress/sleep/energy không có "crisis") — an toàn
        // người dùng không thể phụ thuộc vào việc một LLM có tuân thủ đúng hay không.
        if ("crisis".equals(detectedEmotion)) {
            log.warn("[RAG] CRISIS detected for user={} — trigger hotline response", userId);
            String crisisText = buildCrisisResponse();
            Message crisisMsg = new Message();
            crisisMsg.setConversation(conv);
            crisisMsg.setRole(MessageRole.AI);
            crisisMsg.setContent(crisisText);
            crisisMsg.setDetectedEmotion("sad");
            messageRepo.save(crisisMsg);
            conversationRepo.save(conv);
            return new ChatResponse(null, toResponse(crisisMsg), List.of(), crisisText, "sad", List.of());
        }

        // 1. RETRIEVE — knowledge chunks để feed context cho Gemini (chống bịa thông tin tâm lý).
        // Dùng detectedEmotion cũ (rule-based) + keyword chỉ để TRUY VẤN tài liệu tham khảo —
        // đây không phải là "mood" cuối cùng trả về người dùng, nên không vi phạm yêu cầu
        // "Gemini phải phân tích toàn bộ nội dung" cho việc xác định mood.
        List<KnowledgeDocument> docs = retrieve(detectedEmotion, userMessage);
        log.info("[RAG] Retrieved {} docs, message='{}'",
                 docs.size(), userMessage.substring(0, Math.min(50, userMessage.length())));

        // 2. AUGMENT & PREPARE HISTORY (5-10 tin gần nhất để Gemini hiểu ngữ cảnh nhiều lượt)
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

        String systemInstruction = buildSystemInstruction(docs) + buildAntiRepeatHint(geminiHistory);

        // 3. GENERATE — Gemini trả JSON {"reply","mood","suggestMusic"} (structured output, xem GeminiService).
        // AI CHỈ xác định mood + có nên gợi ý nhạc hay không; KHÔNG tự chọn/bịa bài hát — backend
        // tự truy vấn DB ở bước 4, và CHỈ khi suggestMusic=true (tránh câu nào cũng bị gắn nhạc).
        String rawJson = aiGenerationService.generateChat(systemInstruction, geminiHistory, userMessage);
        GeminiChatOutput parsed = parseGeminiOutput(rawJson);

        String replyText;
        String mood;
        boolean suggestMusic;
        if (parsed != null && parsed.reply() != null && !parsed.reply().isBlank()) {
            String cleaned = sanitizeResponse(parsed.reply());
            replyText = (cleaned != null && !cleaned.isBlank()) ? cleaned : parsed.reply().trim();
            // An toàn tuyệt đối: AI không được tự chèn link nhạc/nguồn nào trong reply — mọi URL
            // (kể cả hợp lệ) đều bị xoá, vì bài hát giờ được đính kèm riêng ở field `songs`.
            replyText = stripAllLinks(replyText);
            mood = normalizeMood(parsed.mood());
            suggestMusic = Boolean.TRUE.equals(parsed.suggestMusic());
        } else {
            // FALLBACK — chỉ khi Gemini lỗi/mất mạng/hết quota (không parse được JSON).
            // Lúc này không còn AI để phân tích mood/ý định nên tạm dùng suy luận rule-based làm
            // lưới đỡ, KHÔNG phải là cơ chế chính (đó luôn là Gemini khi hoạt động bình thường).
            // stripAllLinks() áp dụng luôn ở đây (không chỉ nhánh Gemini) — lưới an toàn phòng khi
            // câu fallback nào đó lỡ chứa URL, khung chat không còn render link nữa nên URL trần
            // sẽ hiện ra như text vỡ giao diện nếu không được xoá.
            replyText = stripAllLinks(buildFallbackResponse(detectedEmotion, docs));
            mood = fallbackMoodFromEmotion(detectedEmotion, userMessage);
            // Không có Gemini để suy luận ý định → chỉ gợi ý nhạc khi user tự xin rõ ràng trong câu.
            suggestMusic = extractRequestedMoodTag(userMessage) != null;
            log.info("[RAG] Dùng fallback response (Gemini không khả dụng), mood fallback='{}'", mood);
        }

        // 4. Backend tự lấy bài hát THẬT từ content_library theo đúng mood — không phải AI chọn,
        // và chỉ khi Gemini (hoặc fallback) xác định lượt chat này thực sự nên kèm nhạc.
        List<ContentLibrary> musicCandidates = List.of();
        List<SongResponse> songs = List.of();
        if (suggestMusic) {
            String moodTag = moodToContentTag(mood);
            musicCandidates = pickMusicCandidates(moodTag);
            songs = musicCandidates.stream().map(this::toSongResponse).toList();
        }

        // ── 5. LƯU tin nhắn AI vào DB (lưu luôn mood vào detected_emotion để giữ lịch sử) ──
        // song_ids lưu lại ĐÚNG những bài đã gợi ý (SongMapper) — để khi load lại lịch sử (F5,
        // chuyển hội thoại) vẫn hiển thị đúng thẻ bài hát này, không bị mất như trước đây (trước
        // chỉ giữ tạm trong state React của phiên chat hiện tại).
        Message aiMessage = new Message();
        aiMessage.setConversation(conv);
        aiMessage.setRole(MessageRole.AI);
        aiMessage.setContent(replyText.trim());
        aiMessage.setDetectedEmotion(mood);
        aiMessage.setSongIds(SongMapper.toIdString(musicCandidates));
        messageRepo.save(aiMessage);
        conversationRepo.save(conv); // cập nhật updatedAt

        // ── 6. Build response ─────────────────────────────────────────────────
        List<String> sources = docs.stream()
                .map(d -> d.getSource() + " — " + d.getTitle())
                .distinct()
                .collect(Collectors.toList());

        return new ChatResponse(null, toResponse(aiMessage), sources, replyText.trim(), mood, songs);
    }

    /**
     * Chat khi CHƯA đăng nhập — dùng lại đúng pipeline Retrieve/Augment/Generate/Fallback, crisis
     * gate, VÀ gợi ý nhạc thật từ content_library như chat() có tài khoản — khách vẫn được nghe
     * nhạc Dora gợi ý bình thường. Điểm khác biệt DUY NHẤT: KHÔNG tạo/lưu Message hay
     * AiConversation nào — lịch sử hội thoại do client tự gửi kèm mỗi lần gọi (giữ tạm trong
     * state của trình duyệt, mất khi tải lại trang), không có gì được ghi vào DB.
     */
    @Override
    public GuestChatResponse chatGuest(String userMessage, String detectedEmotion,
                                        List<Map<String, String>> history) {
        // CRISIS: cùng lưới an toàn tuyệt đối, không phụ thuộc Gemini, áp dụng cả khi chưa đăng nhập.
        if ("crisis".equals(detectedEmotion)) {
            log.warn("[RAG][Guest] CRISIS detected — trigger hotline response");
            return new GuestChatResponse(buildCrisisResponse(), "sad", List.of());
        }

        // 1. RETRIEVE
        List<KnowledgeDocument> docs = retrieve(detectedEmotion, userMessage);

        // 2. AUGMENT — lịch sử từ client, chuẩn hoá role + giới hạn số lượt như bên có tài khoản.
        List<Map<String, String>> geminiHistory = normalizeGuestHistory(history);
        String systemInstruction = buildSystemInstruction(docs) + buildAntiRepeatHint(geminiHistory);

        // 3. GENERATE
        String rawJson = aiGenerationService.generateChat(systemInstruction, geminiHistory, userMessage);
        GeminiChatOutput parsed = parseGeminiOutput(rawJson);

        String replyText;
        String mood;
        boolean suggestMusic;
        if (parsed != null && parsed.reply() != null && !parsed.reply().isBlank()) {
            String cleaned = sanitizeResponse(parsed.reply());
            replyText = (cleaned != null && !cleaned.isBlank()) ? cleaned : parsed.reply().trim();
            replyText = stripAllLinks(replyText);
            mood = normalizeMood(parsed.mood());
            suggestMusic = Boolean.TRUE.equals(parsed.suggestMusic());
        } else {
            // FALLBACK — Gemini lỗi/mất mạng/hết quota. stripAllLinks() áp dụng luôn cho fallback
            // (cùng lý do như bên chat() có tài khoản — khung chat không còn render link nữa).
            replyText = stripAllLinks(buildFallbackResponse(detectedEmotion, docs));
            mood = fallbackMoodFromEmotion(detectedEmotion, userMessage);
            suggestMusic = extractRequestedMoodTag(userMessage) != null;
            log.info("[RAG][Guest] Dùng fallback response (Gemini không khả dụng), mood fallback='{}'", mood);
        }

        // 4. Backend tự lấy bài hát THẬT từ content_library theo đúng mood — giống hệt chat() có
        // tài khoản, chỉ khác là không có song_ids nào được lưu lại (không có Message để gắn vào).
        List<SongResponse> songs = List.of();
        if (suggestMusic) {
            List<ContentLibrary> musicCandidates = pickMusicCandidates(moodToContentTag(mood));
            songs = musicCandidates.stream().map(this::toSongResponse).toList();
        }

        return new GuestChatResponse(replyText.trim(), mood, songs);
    }

    /**
     * Chuẩn hoá lịch sử do client gửi lên (role "user"/"ai") sang định dạng Gemini ("user"/"model"),
     * bỏ qua nội dung rỗng, và chỉ giữ HISTORY_TURNS lượt gần nhất — khớp giới hạn dùng cho chat
     * có tài khoản, tránh client gửi lịch sử quá dài gây tốn chi phí/độ trễ.
     */
    private List<Map<String, String>> normalizeGuestHistory(List<Map<String, String>> clientHistory) {
        if (clientHistory == null || clientHistory.isEmpty()) return List.of();
        int from = Math.max(0, clientHistory.size() - HISTORY_TURNS);
        List<Map<String, String>> result = new ArrayList<>();
        for (Map<String, String> turn : clientHistory.subList(from, clientHistory.size())) {
            String content = turn.get("content");
            if (content == null || content.isBlank()) continue;
            String role = "user".equals(turn.get("role")) ? "user" : "model";
            result.add(Map.of("role", role, "content", content));
        }
        return result;
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
        // Stopwords tiếng Việt + tiếng Anh phổ biến
        Set<String> stopwords = Set.of(
                "tôi", "mình", "bạn", "là", "và", "có", "không",
                "của", "với", "được", "một", "này", "đó", "thì", "để", "từ",
                "trong", "hôm", "nay", "rất", "quá", "cũng", "đang", "hay",
                "cho", "khi", "như", "nên", "vì", "tại", "lại", "nhưng",
                "sao", "đã", "sẽ", "muốn", "thấy", "cần", "làm", "ra",
                "the", "and", "or", "is", "are", "was", "it", "that", "this");

        return Arrays.stream(message.toLowerCase().split("[\\s,\\.!?;:]+"))
                .filter(w -> w.length() > 2 && !stopwords.contains(w))
                .distinct()
                .limit(7)  // tăng từ 5 → 7 để bắt nhiều keyword hơn
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 2: AUGMENT — Build system instruction
    // ─────────────────────────────────────────────────────────────────────────

    private String buildSystemInstruction(List<KnowledgeDocument> docs) {
        StringBuilder sb = new StringBuilder();

        // System prompt — Dora: người bạn tâm sự thông minh, linh hoạt, tự nhiên
        sb.append("""
            Tên bạn là Dora — người bạn tâm sự thân thiết của ứng dụng Mindora, một chatbot chữa lành.
            Bạn ấm áp, thông minh, biết lắng nghe và cũng biết bông đùa đúng lúc.
            Bạn có kiến thức tâm lý (CBT, ACT, Mindfulness) nhưng không bao giờ giảng bài hay lên lớp.

            PHONG CÁCH:
            - Xưng "mình"/"tớ", gọi người dùng là "bạn"/"cậu" — follow cách họ xưng hô.
            - Nhắn tin như bạn bè thật: câu ngắn, tự nhiên, đôi khi hỏi ngược lại.
            - Phản ứng đúng với NỘI DUNG cụ thể của tin nhắn — đừng trả lời chung chung.
            - 2–4 câu là đủ cho hầu hết tình huống. Dài hơn khi thật sự cần.
            - Đừng liệt kê bullet point trừ khi đang gợi ý nhiều bước.
            - Thay đổi cách mở đầu: đồng cảm, hỏi, kể chuyện, đùa nhẹ — tùy tình huống.
            - Đừng lặp lại cấu trúc hay từ ngữ đã dùng ở tin nhắn trước.
            - Giọng điệu luôn tích cực, đồng cảm, không phán xét — dù người dùng nói gì.
            - Tránh câu sáo: "Mình hiểu rồi", "Cảm ơn bạn đã chia sẻ", "Bạn không đơn độc đâu nhé".
              Thay vào đó: hỏi thẳng, phản ứng cụ thể, hoặc chia sẻ điều liên quan tình huống của họ.
            - Khi phù hợp, hãy chủ động đặt câu hỏi tiếp theo để hiểu rõ hơn hoặc giữ mạch trò chuyện
              — đừng chỉ đưa ra một câu trả lời khép kín rồi dừng lại.

            TRUNG THỰC — TUYỆT ĐỐI KHÔNG BỊA THÔNG TIN:
            - Chỉ nói những điều bạn thực sự chắc chắn hoặc có trong phần "kiến thức tham khảo" bên dưới (nếu có).
            - Nếu không chắc chắn về một thông tin, hãy thẳng thắn nói bạn không biết hoặc hỏi lại người dùng
              để hiểu rõ hơn — không đoán mò, không suy diễn thành sự thật.
            - KHÔNG được tự nhắc tên bài hát cụ thể, ca sĩ, album, hay link nhạc/YouTube/Spotify nào trong
              câu trả lời (kể cả khi người dùng xin nhạc) — hệ thống sẽ tự động đính kèm bài hát phù hợp
              từ cơ sở dữ liệu dựa trên mood bạn xác định, bạn chỉ cần trò chuyện bình thường và xác định mood.
            - KHÔNG chẩn đoán, KHÔNG khẳng định người dùng đang mắc một vấn đề sức khỏe tâm thần cụ thể nào
              (vd không nói "bạn bị trầm cảm/rối loạn lo âu..."). Nếu tình trạng có vẻ nghiêm trọng hoặc kéo dài,
              nhẹ nhàng khuyến khích họ tìm gặp chuyên gia tâm lý — đó là gợi ý, không phải phán quyết.

            """);

        sb.append("""
            KHỦNG HOẢNG (tự hại/không muốn sống):
            Ở bên họ, lắng nghe, rồi nhẹ nhàng: "Mình lo cho cậu lắm. Gọi ngay 1800 599 920 nhé — miễn phí, 24/7."

            """);

        // Knowledge context từ RAG
        if (!docs.isEmpty()) {
            sb.append("Một số gợi ý từ kiến thức tâm lý mà bạn có thể lồng ghép tự nhiên (đừng đọc nguyên văn, hãy diễn đạt lại theo cách bạn bè):\n");
            for (int i = 0; i < docs.size(); i++) {
                KnowledgeDocument doc = docs.get(i);
                sb.append("- ").append(doc.getTitle()).append(": ");
                String snippet = doc.getContent().length() > 400
                        ? doc.getContent().substring(0, 400) + "..."
                        : doc.getContent();
                sb.append(snippet).append("\n");
            }
            sb.append("\n");
        }

        // ── XÁC ĐỊNH MOOD — bắt buộc, dùng để backend tự lấy nhạc phù hợp từ DB ──
        sb.append("""
            XÁC ĐỊNH MOOD (bắt buộc, trả trong field "mood" của JSON output):
            Đọc và phân tích TOÀN BỘ nội dung cuộc trò chuyện (không chỉ dựa vào từ khoá đơn lẻ) để xác định
            đúng NHẤT tâm trạng hiện tại của người dùng, chọn CHÍNH XÁC một trong 6 giá trị sau:
            - happy:  đang vui vẻ, tích cực, hào hứng, có tin vui
            - calm:   bình thường, muốn thư giãn/thiền, hoặc không có tâm trạng đặc biệt rõ ràng
            - sad:    đang buồn, thất vọng, cô đơn, vừa trải qua mất mát/chia tay
            - stress: đang lo âu, căng thẳng, áp lực, hoảng loạn, tức giận, quá tải
            - sleep:  đang mệt mỏi, mất ngủ, muốn ngủ, kiệt sức, cần thư giãn để dễ ngủ
            - energy: cần thêm năng lượng, muốn vận động/tập luyện, tràn đầy sức sống
            Nếu không rõ ràng, hãy chọn "calm" làm mặc định. Trường "mood" LUÔN LUÔN phải là một trong 6 giá trị
            trên, không được để trống, không được bịa giá trị khác.

            """);

        // ── QUYẾT ĐỊNH CÓ GỢI Ý NHẠC HAY KHÔNG — bắt buộc, trả trong field "suggestMusic" ──
        sb.append("""
            QUYẾT ĐỊNH CÓ GỢI Ý NHẠC HAY KHÔNG (bắt buộc, trả boolean trong field "suggestMusic"):
            KHÔNG phải lượt trò chuyện nào cũng nên kèm nhạc — chỉ đính kèm khi thực sự hữu ích, giống
            một người bạn tinh tế biết lúc nào nên đề xuất, lúc nào chỉ cần lắng nghe/trò chuyện thôi.

            Đặt suggestMusic = true khi (chỉ cần đúng 1 trong các trường hợp sau):
            - Người dùng CHỦ ĐỘNG xin nhạc/bài hát/playlist một cách rõ ràng (vd "cho mình nghe nhạc",
              "gợi ý bài hát đi", "có bài nào thư giãn không").
            - Người dùng vừa chia sẻ một cảm xúc RÕ RÀNG và MẠNH (rất buồn, rất căng thẳng, mất ngủ, vui
              hào hứng...) và một bài nhạc phù hợp lúc này sẽ thực sự an ủi/hỗ trợ họ — không phải chỉ vì
              có từ khoá cảm xúc thoáng qua.

            Đặt suggestMusic = false cho MỌI trường hợp còn lại, đặc biệt:
            - Tin nhắn chào hỏi, hỏi thăm thông thường, hoặc chưa rõ tâm trạng.
            - Đang giữa một cuộc trò chuyện cần hỏi thêm/lắng nghe (chưa phải lúc chốt lại bằng nhạc).
            - Vừa gợi ý nhạc ở lượt trước rồi (xem lịch sử hội thoại) — đừng lặp lại gợi ý nhạc liên tục
              nhiều lượt liền, trừ khi người dùng xin thêm.
            - Chủ đề trò chuyện không liên quan đến cảm xúc/tâm trạng cần thư giãn.

            Mặc định là false nếu không chắc chắn — thà bỏ lỡ một cơ hội gợi ý nhạc còn hơn làm phiền
            người dùng bằng nhạc không đúng lúc.

            """);

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

    // ─────────────────────────────────────────────────────────────────────────
    // MOOD — parse/validate output của Gemini, map sang mood_tag của content_library
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Parse chuỗi JSON Gemini trả về ("{"reply":"...","mood":"..."}"). Nhờ Gemini structured
     * output (responseSchema ở GeminiService) nên gần như luôn hợp lệ, nhưng vẫn bọc try/catch
     * để không bao giờ crash nếu Gemini trả rỗng/lỗi mạng (aiText null) hoặc JSON dị dạng.
     */
    private GeminiChatOutput parseGeminiOutput(String rawJson) {
        if (rawJson == null || rawJson.isBlank()) return null;
        try {
            return objectMapper.readValue(rawJson, GeminiChatOutput.class);
        } catch (Exception e) {
            log.error("[RAG] Không parse được JSON từ Gemini: {} — raw='{}'", e.getMessage(), rawJson);
            return null;
        }
    }

    /** Đảm bảo mood luôn là 1 trong 6 giá trị hợp lệ — Gemini lệch chuẩn (hiếm) thì fallback "calm". */
    private String normalizeMood(String mood) {
        if (mood == null) return "calm";
        String m = mood.trim().toLowerCase();
        return VALID_MOODS.contains(m) ? m : "calm";
    }

    /**
     * content_library.mood_tag chỉ có 5 giá trị (calm/sleep/happy/sad/energy) — không có "stress".
     * "stress" là trạng thái cần làm dịu nên map về nhạc "calm".
     */
    private String moodToContentTag(String mood) {
        return "stress".equals(mood) ? "calm" : mood;
    }

    /**
     * Chỉ dùng khi Gemini không khả dụng (mất mạng/hết quota) — không phải cơ chế xác định mood
     * chính. Ưu tiên 1: user tự nêu rõ muốn nhạc kiểu gì (vd "cho mình nhạc vui"). Ưu tiên 2: suy
     * từ emotion rule-based cũ. Không có gì rõ ràng → "calm".
     */
    private String fallbackMoodFromEmotion(String emotion, String userMessage) {
        String requested = extractRequestedMoodTag(userMessage);
        if (requested != null) return requested;
        return switch (emotion) {
            case "anxious", "angry" -> "stress";
            case "sad"              -> "sad";
            case "tired"            -> "sleep";
            case "happy"            -> "happy";
            default                 -> "calm";
        };
    }

    private static final Pattern URL_PATTERN = Pattern.compile("https?://\\S+");

    /**
     * AI không được tự chèn bài hát/link nào trong reply (bài hát giờ đính kèm riêng ở field
     * `songs`, backend tự lấy từ DB) — đây là lưới an toàn cuối cùng, xoá sạch mọi URL nếu
     * Gemini lỡ vi phạm hướng dẫn trong prompt.
     */
    private String stripAllLinks(String text) {
        if (text == null || text.isBlank()) return text;
        return URL_PATTERN.matcher(text).replaceAll("").replaceAll("[ \\t]{2,}", " ").trim();
    }

    private SongResponse toSongResponse(ContentLibrary c) {
        String title = c.getTitle();
        String artist = "Nhạc Việt";
        int dash = title.indexOf('—');
        if (dash >= 0) {
            artist = title.substring(dash + 1).trim();
            title = title.substring(0, dash).trim();
        }
        return new SongResponse(c.getId(), title, artist, c.getYoutubeId(), c.getMoodTag());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // NHẠC — chọn bài thật từ content_library theo tâm trạng (thay vì hardcode)
    // ─────────────────────────────────────────────────────────────────────────

    /** Số bài tối đa đính kèm mỗi lượt chat — đủ đa dạng nhưng không gây rối UI. */
    private static final int MAX_MUSIC_CANDIDATES = 3;

    /**
     * Lấy tối đa MAX_MUSIC_CANDIDATES bài nhạc đang active, đúng mood, CÓ youtube_id
     * (chỉ nguồn phát duy nhất hiện dùng — không còn Spotify/SoundHelix demo),
     * xáo ngẫu nhiên để mỗi lượt chat Dora có thể gợi ý bài khác nhau thay vì lặp mãi 1 bài.
     */
    private List<ContentLibrary> pickMusicCandidates(String moodTag) {
        if (moodTag == null) return List.of();

        Page<ContentLibrary> page = contentRepo.findByIsActiveTrueAndMoodTagAndContentType(
                moodTag, "music", PageRequest.of(0, 30));

        List<ContentLibrary> pool = page.getContent().stream()
                .filter(c -> c.getYoutubeId() != null && !c.getYoutubeId().isBlank())
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.shuffle(pool, RANDOM);
        return pool.stream().limit(MAX_MUSIC_CANDIDATES).collect(Collectors.toList());
    }

    /**
     * Nếu user tự nêu rõ muốn nghe kiểu nhạc gì ngay trong câu này (vd "cho mình nhạc vui lên tí",
     * "có bài nào ru ngủ không") — CHỈ dùng trong nhánh fallback khi Gemini không khả dụng.
     * Check "sleep" trước "sad" vì "buồn ngủ" chứa từ "buồn" nhưng thực chất là buồn ngủ, không phải buồn bã.
     */
    private String extractRequestedMoodTag(String message) {
        if (message == null || message.isBlank()) return null;
        String lower = message.toLowerCase();

        if (containsAny(lower, "buồn ngủ", "buon ngu", "khó ngủ", "kho ngu", "mất ngủ", "mat ngu",
                "ngủ ngon", "ngu ngon", "đi ngủ", "di ngu", "ru ngủ", "ru ngu", "dễ ngủ", "de ngu")) {
            return "sleep";
        }
        if (containsAny(lower, "năng lượng", "nang luong", "tập luyện", "tap luyen", "tập gym", "gym",
                "chạy bộ", "chay bo", "workout", "hưng phấn", "hung phan", "sung", "energy")) {
            return "energy";
        }
        if (containsAny(lower, "vui", "hạnh phúc", "hanh phuc", "tích cực", "tich cuc",
                "phấn khởi", "phan khoi", "happy")) {
            return "happy";
        }
        if (containsAny(lower, "buồn", "buon", "khóc", "khoc", "sad")) {
            return "sad";
        }
        if (containsAny(lower, "thư giãn", "thu gian", "chill", "bình tĩnh", "binh tinh",
                "thiền", "thien", "yên tĩnh", "yen tinh", "calm")) {
            return "calm";
        }
        return null;
    }

    private boolean containsAny(String text, String... keywords) {
        for (String k : keywords) {
            if (text.contains(k)) return true;
        }
        return false;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CRISIS RESPONSE — Luôn có hotline, không phụ thuộc Gemini
    // ─────────────────────────────────────────────────────────────────────────

    private static final List<String> CRISIS_RESPONSES = List.of(
        "Mình đọc tin của cậu và thực sự lo cho cậu. Cậu không phải đối mặt một mình đâu — có người sẵn sàng lắng nghe cậu ngay bây giờ.\n\n" +
        "**Đường dây hỗ trợ khủng hoảng miễn phí 24/7:**\n" +
        "• Ngày Mai (Việt Nam): **1800 599 920**\n" +
        "• Tổng đài quốc gia bảo vệ trẻ em: **111**\n\n" +
        "Cậu có thể gọi ngay bây giờ. Mình vẫn ở đây với cậu.",

        "Cảm ơn cậu đã nói ra điều này với mình — thật sự cần dũng cảm lắm. Cảm giác đau đớn cậu đang trải qua là có thật, nhưng nó không phải là mãi mãi.\n\n" +
        "Ngay lúc này, hãy để một người thật lắng nghe cậu:\n" +
        "**1800 599 920** (Ngày Mai — miễn phí, 24/7, kín đáo)\n\n" +
        "Cậu có thể ở đây với mình trong lúc gọi không?",

        "Mình muốn cậu biết: cảm xúc này rất nặng nhưng cậu không cô đơn. Có những người được đào tạo để đồng hành cùng cậu ngay lúc này.\n\n" +
        "Gọi ngay **1800 599 920** — Ngày Mai (đường dây hỗ trợ khủng hoảng, miễn phí, 24/7).\n" +
        "Nếu cậu đang gặp nguy hiểm ngay lập tức, hãy gọi **115** (cấp cứu).\n\n" +
        "Cậu là quan trọng với mình. Mình vẫn ở đây."
    );

    private String buildCrisisResponse() {
        return CRISIS_RESPONSES.get(RANDOM.nextInt(CRISIS_RESPONSES.size()));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FALLBACK — Khi không có API key hoặc Gemini lỗi
    // ─────────────────────────────────────────────────────────────────────────

    // Fallback responses đa dạng — chọn ngẫu nhiên để tránh lặp
    private static final Map<String, List<String>> FALLBACK_POOL = new LinkedHashMap<>();
    static {
        FALLBACK_POOL.put("anxious", List.of(
            "Nghe có vẻ căng lắm đấy... Bạn thở sâu cùng mình một cái nhé? Hít vào 4 giây, giữ 4 giây, thở ra 4 giây — cứ thế vài lần xem sao.",
            "Cái cảm giác lo lắng này nó nặng lắm, mình biết. Thử kể cụ thể hơn cho mình nghe xem — chuyện gì đang khiến bạn căng nhất lúc này?",
            "Bạn đang bị áp lực từ nhiều phía quá rồi. Một mẹo nhỏ: viết ra giấy 3 thứ đang lo nhất — cứ viết ra thôi, không cần giải quyết ngay. Não mình hay bớt căng hơn khi làm vậy đó.",
            "Stress kiểu này thường là dấu hiệu bạn đang cố gánh quá nhiều một mình. Có điều gì bạn có thể bỏ bớt hoặc nhờ người khác giúp không?",
            "Thử nghe một bản nhạc nhẹ nhàng xem có dịu hơn không — mở tab Khám phá là có ngay vài gợi ý phù hợp lúc này."
        ));
        FALLBACK_POOL.put("sad", List.of(
            "Buồn thì cứ buồn đi, không cần phải giả vờ ổn đâu. Mình ở đây — bạn muốn kể không?",
            "Đôi khi không cần lý do để buồn đâu, cảm xúc nó vốn thế. Hôm nay bạn thấy nặng nề từ lúc nào vậy?",
            "Mình đang ngồi đây với bạn nè. Kể đi, kể gì cũng được — mình nghe hết.",
            "Có những lúc chỉ cần nghe một bản nhạc và để nước mắt chảy một chút cũng đã bớt rất nhiều — thử ghé tab Khám phá xem có bài nào hợp tâm trạng này không.",
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
            "Thử nghe một bản nhạc êm rồi chợp mắt một chút nhé — tab Khám phá có vài bài giúp dễ ngủ hơn đó.",
            "Kiệt sức không phải yếu đuối — đó là dấu hiệu bạn đã cố quá lâu rồi. Bạn cần gì nhất lúc này?",
            "Mình biết bạn đang cố gắng hết sức, nhưng cơ thể đang nhắn tin cho bạn đó: cần nghỉ. Tối nay bạn ngủ được không?"
        ));
        FALLBACK_POOL.put("happy", List.of(
            "Ôi hay quá! Kể mình nghe chuyện gì vui vậy?",
            "Nghe vui hẳn lên luôn! Năng lượng tích cực của bạn lan sang mình rồi đây",
            "Tốt quá! Cảm giác này thật sự đáng giữ lại lắm đó. Hôm nay có điều gì đặc biệt không?",
            "Mình vui vì bạn vui! Hãy tận hưởng khoảnh khắc này thật trọn vẹn nhé.",
            "Đây là năng lượng mình thích thấy ở bạn! Tiếp tục giữ vậy nha"
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
        // Chi dung cau tu pool - ngan gon, tu nhien nhu ban be nhan tin.
        // Knowledge docs chi de feed vao Gemini prompt lam context, khong dump truc tiep cho user.
        List<String> pool = FALLBACK_POOL.getOrDefault(emotion, FALLBACK_POOL.get("neutral"));
        return pool.get(RANDOM.nextInt(pool.size()));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST-PROCESSING — Lọc câu sáo rỗng mà Gemini hay sinh ra
    // ─────────────────────────────────────────────────────────────────────────

    // Các mẫu câu mở đầu sáo rỗng — sẽ bị xóa khỏi đầu response
    private static final List<String> CLICHE_OPENERS = List.of(
        "mình hiểu rồi",
        "mình hiểu bạn",
        "tớ hiểu rồi",
        "tớ hiểu bạn",
        "cảm ơn bạn đã chia sẻ",
        "cảm ơn bạn đã tin tưởng",
        "cảm ơn bạn đã luôn tin tưởng",
        "cảm ơn bạn đã tâm sự",
        "cảm ơn cậu đã chia sẻ",
        "cảm ơn cậu đã tin tưởng",
        "tôi hiểu bạn",
        "là một ai",
        "mindora hiểu rằng",
        "mình rất vui khi",
        "thật sự rất vui khi"
    );

    // Các mẫu câu kết sáo rỗng — sẽ bị xóa khỏi cuối response
    private static final List<String> CLICHE_CLOSERS = List.of(
        "bạn muốn tâm sự thêm điều gì không",
        "bạn có muốn chia sẻ thêm không",
        "bạn muốn kể thêm không",
        "cậu muốn tâm sự thêm điều gì không",
        "cậu có muốn chia sẻ thêm không",
        "mình luôn ở đây lắng nghe bạn",
        "mình luôn ở đây lắng nghe cậu",
        "bạn không đơn độc đâu nhé",
        "bạn không cô đơn đâu nhé",
        "cậu không đơn độc đâu nhé",
        "hãy nhớ rằng bạn không cô đơn",
        "bạn có thể tin tưởng vào mình",
        "mình luôn ở đây vì bạn"
    );

    /**
     * Xóa câu mở đầu/kết thúc sáo rỗng mà Gemini hay lặp lại.
     * Tách thành từng câu (kể cả khi có emoji), lọc câu vi phạm, ghép lại.
     */
    private String sanitizeResponse(String text) {
        if (text == null || text.isBlank()) return null;

        String[] sentences = text.split("[.!?…\n]+\\s*|(?<=\\p{So})\\s+");
        List<String> filtered = new ArrayList<>();

        for (String sentence : sentences) {
            if (sentence.isBlank()) continue;
            // Normalize NFC để tránh lỗi so sánh Unicode tiếng Việt từ Gemini
            String lower = Normalizer.normalize(sentence.toLowerCase().trim(), Normalizer.Form.NFC);
            boolean isCliche = false;

            for (String opener : CLICHE_OPENERS) {
                if (lower.startsWith(opener)) {
                    isCliche = true;
                    log.debug("[Sanitize] Removed cliché opener: '{}'", sentence.trim());
                    break;
                }
            }

            if (!isCliche) {
                for (String closer : CLICHE_CLOSERS) {
                    if (lower.contains(closer)) {
                        isCliche = true;
                        log.debug("[Sanitize] Removed cliché closer: '{}'", sentence.trim());
                        break;
                    }
                }
            }

            if (!isCliche) {
                filtered.add(sentence.trim());
            }
        }

        if (filtered.isEmpty()) {
            log.warn("[Sanitize] Tất cả câu đều bị lọc — fallback về pool");
            return null;
        }

        return String.join(" ", filtered).trim();
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
                m.getCreatedAt(),
                SongMapper.resolve(m.getSongIds(), contentRepo));
    }
}
