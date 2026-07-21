package com.mindora.service.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindora.dto.response.AchievementHistoryItem;
import com.mindora.dto.response.AchievementInfo;
import com.mindora.dto.response.DailyInsightResponse;
import com.mindora.dto.response.StreakInfo;
import com.mindora.dto.response.TreeInfo;
import com.mindora.dto.response.WeeklySummaryResponse;
import com.mindora.dto.response.XpInfo;
import com.mindora.entity.DailyInsight;
import com.mindora.entity.Message;
import com.mindora.entity.MessageRole;
import com.mindora.entity.User;
import com.mindora.entity.UserGamification;
import com.mindora.exception.ResourceNotFoundException;
import com.mindora.repository.DailyInsightRepository;
import com.mindora.repository.MessageRepository;
import com.mindora.repository.UserGamificationRepository;
import com.mindora.repository.UserRepository;
import com.mindora.service.AiGenerationService;
import com.mindora.service.DailyInsightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * "Khoảnh khắc khám phá mỗi ngày" — điểm khác biệt chính của Dashboard so với các app mood-tracker
 * thông thường: thay vì chỉ có biểu đồ, Gemini đọc lại TOÀN BỘ đoạn chat của user trong ngày và
 * trả về 1 nhận xét cụ thể, cá nhân hoá — tạo cảm giác tò mò "hôm nay AI sẽ nhận ra điều gì về
 * mình?" khiến user muốn quay lại mỗi ngày.
 *
 * NGUYÊN TẮC QUAN TRỌNG: AI chỉ tạo NỘI DUNG định tính (mood_score đánh giá cảm xúc, summary,
 * insight, next_tip, pet_message, daily_title, tên/mô tả huy hiệu) — mọi CON SỐ GAMIFICATION
 * (XP, level, streak, tree growth, việc có mở achievement hay không) đều do BACKEND tự tính bằng
 * công thức xác định từ tín hiệu đo lường được (số tin nhắn, độ dài chia sẻ, ngày hoạt động liên
 * tiếp...), KHÔNG đọc số liệu này từ Gemini — tránh việc AI "tự phát" điểm thưởng không nhất quán.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DailyInsightServiceImpl implements DailyInsightService {

    private final UserRepository            userRepository;
    private final MessageRepository         messageRepository;
    private final DailyInsightRepository    dailyInsightRepository;
    private final UserGamificationRepository gamificationRepository;
    private final AiGenerationService        aiGenerationService;
    private final ObjectMapper               objectMapper;

    /** Cần ít nhất từng này tin nhắn trong ngày mới đủ dữ liệu để phân tích (tránh gọi Gemini vô ích). */
    private static final int MIN_MESSAGES_TO_ANALYZE = 2;

    private static final int MAX_XP_PER_DAY = 30;
    private static final int MAX_TREE_GROWTH_PER_DAY = 5;
    private static final int XP_PER_LEVEL = 100;

    // Mốc streak được xem là "đáng ăn mừng" — 1 trong các điều kiện khách quan để mở Achievement.
    private static final Set<Integer> STREAK_MILESTONES = Set.of(3, 7, 14, 21, 30, 50, 100);

    // Ngày XP đạt mức này trở lên được xem là "ngày chia sẻ tích cực nổi bật".
    private static final int HIGH_XP_THRESHOLD = 20;

    @Override
    @Transactional
    public DailyInsightResponse getTodayInsight(UUID userId) {
        LocalDate today = LocalDate.now();
        UserGamification gam = getOrCreateGamification(userId);

        Optional<DailyInsight> existing = dailyInsightRepository.findByUserIdAndInsightDate(userId, today);
        if (existing.isPresent()) {
            return buildResponse(today, existing.get(), gam);
        }

        List<Message> todayMessages = fetchTodayMessages(userId);
        if (todayMessages.size() < MIN_MESSAGES_TO_ANALYZE) {
            // Chưa đủ dữ liệu — không gọi Gemini, để FE hiển thị lời mời trò chuyện thay vì insight.
            return buildResponse(today, null, gam);
        }

        // XP hôm nay được BACKEND tự tính từ số liệu tin nhắn thật — không phụ thuộc Gemini.
        int xpEarned = computeXp(todayMessages);

        String rawJson = aiGenerationService.generateJson(buildPrompt(todayMessages), DAILY_INSIGHT_SCHEMA);
        GeminiInsightOutput parsed = parse(rawJson);
        if (parsed == null) {
            // Gemini lỗi/mất mạng — KHÔNG lưu DailyInsight (để lần gọi sau trong ngày có thể thử lại).
            log.warn("[DailyInsight] Không phân tích được cho user={} — Gemini lỗi hoặc trả JSON không hợp lệ", userId);
            return buildResponse(today, null, gam);
        }

        DailyInsight saved = applyAndSave(userId, today, parsed, gam, xpEarned);
        return buildResponse(today, saved, gam);
    }

    @Override
    public List<AchievementHistoryItem> getAchievementHistory(UUID userId) {
        // Chỉ hiển thị huy hiệu trong THÁNG HIỆN TẠI — sang tháng mới, bộ sưu tập tự "reset" để
        // tạo động lực bắt đầu lại thay vì 1 danh sách dài vô hạn không bao giờ thay đổi cảm giác.
        // Dữ liệu cũ vẫn được giữ nguyên trong DB (không xóa), chỉ không hiển thị ở gallery nữa.
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        return dailyInsightRepository.findByUserIdAndInsightDateBetween(userId, monthStart, today).stream()
                .filter(d -> Boolean.TRUE.equals(d.getAchievementUnlocked()))
                .sorted(java.util.Comparator.comparing(DailyInsight::getInsightDate).reversed())
                .map(d -> new AchievementHistoryItem(d.getInsightDate(), d.getAchievementTitle(), d.getAchievementDescription()))
                .toList();
    }

    @Override
    public WeeklySummaryResponse getWeeklySummary(UUID userId) {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6); // 7 ngày kể cả hôm nay

        List<DailyInsight> entries = dailyInsightRepository.findByUserIdAndInsightDateBetween(userId, weekAgo, today);
        UserGamification gam = getOrCreateGamification(userId);

        int totalXp = entries.stream().mapToInt(d -> d.getXpEarned() != null ? d.getXpEarned() : 0).sum();
        int treeGrowthTotal = entries.stream().mapToInt(d -> d.getTreeGrowthDelta() != null ? d.getTreeGrowthDelta() : 0).sum();
        int achievementsUnlocked = (int) entries.stream().filter(d -> Boolean.TRUE.equals(d.getAchievementUnlocked())).count();

        java.util.OptionalDouble avgMoodOpt = entries.stream()
                .map(DailyInsight::getMoodScore)
                .filter(java.util.Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average();
        Double avgMood = avgMoodOpt.isPresent() ? avgMoodOpt.getAsDouble() : null;

        DailyInsight bestDay = entries.stream()
                .filter(d -> d.getMoodScore() != null)
                .max(java.util.Comparator.comparingInt(DailyInsight::getMoodScore))
                .orElse(null);

        return new WeeklySummaryResponse(
                weekAgo,
                today,
                entries.size(),
                totalXp,
                achievementsUnlocked,
                treeGrowthTotal,
                avgMood,
                bestDay != null ? bestDay.getInsightDate() : null,
                bestDay != null ? bestDay.getDailyTitle() : null,
                bestDay != null ? bestDay.getMoodScore() : null,
                gam.getCurrentStreak(),
                gam.getLongestStreak()
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // XP — BACKEND tự tính từ số liệu tin nhắn thật, KHÔNG dùng số do AI gợi ý
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Công thức XP (tối đa {@value #MAX_XP_PER_DAY}, khớp đúng 5 + 15 + 10):
     *   +5   cố định  — đã có 1 cuộc trò chuyện thực chất hôm nay (đủ điều kiện phân tích)
     *   +1/tin nhắn của user, tối đa 15 — thưởng cho việc chủ động nhắn, chặn ở 15 để tránh
     *        "cày" XP bằng cách spam nhiều tin ngắn liên tục
     *   +10  nếu tổng độ dài các tin nhắn của user hôm nay ≥ 300 ký tự (chia sẻ sâu, chi tiết)
     *   +5   nếu tổng độ dài từ 120-299 ký tự (chia sẻ vừa phải)
     * Đây là công thức xác định (deterministic) — cùng 1 input luôn ra cùng 1 kết quả, không có
     * yếu tố AI nào tham gia vào con số cuối cùng.
     */
    private int computeXp(List<Message> todayMessages) {
        long userMessageCount = todayMessages.stream()
                .filter(m -> m.getRole() == MessageRole.USER)
                .count();
        int totalUserChars = todayMessages.stream()
                .filter(m -> m.getRole() == MessageRole.USER)
                .mapToInt(m -> m.getContent() != null ? m.getContent().length() : 0)
                .sum();

        int xp = 5;
        xp += (int) Math.min(userMessageCount, 15);
        if (totalUserChars >= 300) {
            xp += 10;
        } else if (totalUserChars >= 120) {
            xp += 5;
        }

        return clamp(xp, 0, MAX_XP_PER_DAY);
    }

    /** Cây cảm xúc tăng theo đúng tỉ lệ với XP hôm nay — 30 XP tối đa tương ứng 5% tối đa. */
    private int computeTreeDelta(int xpEarned) {
        return clamp(Math.round(xpEarned / 6.0f), 0, MAX_TREE_GROWTH_PER_DAY);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Gom tin nhắn hôm nay + build prompt
    // ─────────────────────────────────────────────────────────────────────────

    private List<Message> fetchTodayMessages(UUID userId) {
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);
        Instant start = today.atStartOfDay(zone).toInstant();
        Instant end = today.plusDays(1).atStartOfDay(zone).toInstant();
        return messageRepository.findByUserIdAndCreatedAtBetween(userId, start, end);
    }

    private String buildPrompt(List<Message> messages) {
        StringBuilder transcript = new StringBuilder();
        // Giới hạn 60 tin nhắn gần nhất trong ngày — vừa đủ ngữ cảnh, vừa tránh tốn token quá mức.
        int startIdx = Math.max(0, messages.size() - 60);
        for (int i = startIdx; i < messages.size(); i++) {
            Message m = messages.get(i);
            String speaker = m.getRole() == MessageRole.AI ? "Dora" : "Bạn";
            String content = m.getContent() == null ? "" : m.getContent();
            if (content.length() > 500) content = content.substring(0, 500) + "...";
            transcript.append(speaker).append(": ").append(content).append("\n");
        }

        return """
            Phân tích cuộc trò chuyện hôm nay giữa người dùng và Dora (chatbot chữa lành của Mindora), trả về JSON.
            Chỉ dựa trên nội dung cuộc trò chuyện THẬT bên dưới — không bịa thêm chi tiết không có trong đó.
            Lưu ý: bạn KHÔNG cần và KHÔNG được tính điểm thưởng/XP/cấp độ — hệ thống backend tự tính các
            con số đó dựa trên dữ liệu thật, bạn chỉ cần tập trung viết nội dung phân tích bên dưới.

            Quy tắc:
            - mood_score (0-100) và emotion: đánh giá tổng quát tâm trạng trong ngày dựa trên toàn bộ hội thoại.
            - insight: MỘT nhận xét cụ thể, cá nhân hoá — trích dẫn hoặc nhắc lại chi tiết/chủ đề/từ ngữ THẬT mà
              người dùng đã nói, cho thấy một sự thay đổi hoặc điểm đáng chú ý (vd tần suất nhắc một chủ đề, sự
              thay đổi trong cách dùng từ...). Tuyệt đối không viết chung chung, sáo rỗng kiểu "bạn đã chia sẻ
              nhiều điều thú vị hôm nay".
            - next_tip: một gợi ý nhỏ, thực tế, làm được ngay cho ngày mai.
            - streak_title: một tên ngắn, ấm áp cho chuỗi ngày đồng hành (vd "Chuỗi kiên trì").
            - achievement.title/description: hãy viết như thể đang trao 1 huy hiệu ghi nhận nỗ lực/tiến bộ của
              người dùng hôm nay, dựa trên nội dung trò chuyện thật (vd vượt qua một nỗi lo, kiên trì dù mệt,
              có góc nhìn tích cực hơn...). Viết có ý nghĩa và cụ thể — hệ thống sẽ tự quyết định lúc nào thực
              sự hiển thị huy hiệu này cho người dùng, bạn cứ viết tốt nhất có thể dựa trên hôm nay.
            - pet_message: ĐÚNG 1 câu ngắn, giọng điệu ấm áp như một thú cưng ảo đang quan tâm người dùng, ví dụ:
              "Momo rất vui vì hôm nay bạn bình tĩnh hơn."
            - daily_title: tiêu đề ngắn, thú vị, tích cực cho hôm nay (vd "Chiến thắng nỗi lo", "Ngày bình yên",
              "Thêm một bước trưởng thành", "Hôm nay bạn đã cố gắng").
            - Không chẩn đoán, không khẳng định người dùng mắc vấn đề tâm lý cụ thể nào.

            Cuộc trò chuyện hôm nay:
            %s
            """.formatted(transcript.toString().trim());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Gemini structured output schema + parsing — chỉ còn nội dung định tính, không còn số liệu
    // gamification (xp/tree_growth/unlock đã bị bỏ khỏi schema, backend tự tính hết).
    // ─────────────────────────────────────────────────────────────────────────

    private static final Map<String, Object> DAILY_INSIGHT_SCHEMA = Map.ofEntries(
        Map.entry("type", "OBJECT"),
        Map.entry("properties", Map.ofEntries(
            Map.entry("mood_score", Map.of("type", "INTEGER", "description", "Điểm tâm trạng tổng quát trong ngày, 0-100.")),
            Map.entry("emotion", Map.of("type", "STRING", "description", "Cảm xúc chủ đạo trong ngày, một cụm từ ngắn.")),
            Map.entry("summary", Map.of("type", "STRING", "description", "Tóm tắt ngắn gọn nội dung trò chuyện hôm nay.")),
            Map.entry("insight", Map.of("type", "STRING", "description", "Nhận xét cụ thể, cá nhân hoá dựa trên nội dung THẬT của cuộc trò chuyện.")),
            Map.entry("next_tip", Map.of("type", "STRING", "description", "Một gợi ý nhỏ, thực tế cho ngày mai.")),
            Map.entry("streak_title", Map.of("type", "STRING", "description", "Tên ngắn, ấm áp cho chuỗi ngày đồng hành.")),
            Map.entry("achievement", Map.of(
                "type", "OBJECT",
                "properties", Map.of(
                    "title", Map.of("type", "STRING", "description", "Tên huy hiệu ngắn, có ý nghĩa dựa trên hôm nay."),
                    "description", Map.of("type", "STRING", "description", "Mô tả 1 câu lý do ghi nhận.")
                ),
                "required", List.of("title", "description")
            )),
            Map.entry("pet_message", Map.of("type", "STRING", "description", "Đúng 1 câu ngắn từ thú cưng ảo, giọng ấm áp.")),
            Map.entry("daily_title", Map.of("type", "STRING", "description", "Tiêu đề ngắn, tích cực cho hôm nay."))
        )),
        Map.entry("required", List.of(
            "mood_score", "emotion", "summary", "insight", "next_tip",
            "streak_title", "achievement", "pet_message", "daily_title"
        ))
    );

    private record AchievementOut(String title, String description) {}

    private record GeminiInsightOutput(
            @JsonProperty("mood_score") Integer moodScore,
            String emotion,
            String summary,
            String insight,
            @JsonProperty("next_tip") String nextTip,
            @JsonProperty("streak_title") String streakTitle,
            AchievementOut achievement,
            @JsonProperty("pet_message") String petMessage,
            @JsonProperty("daily_title") String dailyTitle
    ) {}

    private GeminiInsightOutput parse(String rawJson) {
        if (rawJson == null || rawJson.isBlank()) return null;
        try {
            return objectMapper.readValue(rawJson, GeminiInsightOutput.class);
        } catch (Exception e) {
            log.error("[DailyInsight] Không parse được JSON từ Gemini: {} — raw='{}'", e.getMessage(), rawJson);
            return null;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Áp dụng kết quả: XP/streak/level/tree/achievement 100% do BACKEND tính — AI chỉ cung cấp
    // nội dung văn bản (insight, tên huy hiệu, lời nhắn pet...).
    // ─────────────────────────────────────────────────────────────────────────

    private DailyInsight applyAndSave(UUID userId, LocalDate today, GeminiInsightOutput parsed,
                                       UserGamification gam, int xpEarned) {
        int treeDelta = computeTreeDelta(xpEarned);

        // Streak: luôn do backend xác định dựa trên lastActiveDate.
        LocalDate lastActive = gam.getLastActiveDate();
        if (lastActive != null && lastActive.equals(today.minusDays(1))) {
            gam.setCurrentStreak(gam.getCurrentStreak() + 1);
        } else {
            // Chưa từng hoạt động, hoặc chuỗi đã đứt (bỏ lỡ >= 1 ngày) — bắt đầu lại từ 1.
            gam.setCurrentStreak(1);
        }
        boolean streakIncreased = true; // luôn true khi có insight hôm nay (đã đủ điều kiện phân tích)
        gam.setLastActiveDate(today);
        gam.setLongestStreak(Math.max(gam.getLongestStreak(), gam.getCurrentStreak()));

        // Level TRƯỚC khi cộng XP hôm nay — dùng để phát hiện có lên level hay không.
        int levelBefore = gam.getXp() / XP_PER_LEVEL + 1;

        // Tree growth: cộng dồn, tự "lên cây mới" khi chạm mốc 100%.
        int newPercent = gam.getTreeGrowthPercent() + treeDelta;
        boolean treeBloomed = newPercent >= 100;
        if (treeBloomed) {
            gam.setTreeCount(gam.getTreeCount() + 1);
            newPercent -= 100;
        }
        gam.setTreeGrowthPercent(Math.max(0, newPercent));

        // XP + level (lên level mỗi 100 XP).
        gam.setXp(gam.getXp() + xpEarned);
        gam.setLevel(gam.getXp() / XP_PER_LEVEL + 1);
        boolean leveledUp = gam.getLevel() > levelBefore;

        // Achievement: mở khi đạt BẤT KỲ điều kiện khách quan nào dưới đây — hoàn toàn do backend
        // quyết định (không đọc cờ "unlock" từ AI). Nội dung (title/description) vẫn lấy từ AI vì
        // đó là phần cần hiểu ngữ cảnh cuộc trò chuyện để viết cho có ý nghĩa.
        boolean streakMilestone = STREAK_MILESTONES.contains(gam.getCurrentStreak());
        boolean highXpDay = xpEarned >= HIGH_XP_THRESHOLD;
        boolean unlockAchievement = leveledUp || streakMilestone || treeBloomed || highXpDay;

        gamificationRepository.save(gam);

        DailyInsight entity = new DailyInsight();
        entity.setUser(userRepository.getReferenceById(userId));
        entity.setInsightDate(today);
        entity.setMoodScore(parsed.moodScore());
        entity.setEmotion(parsed.emotion());
        entity.setSummary(parsed.summary());
        entity.setInsight(parsed.insight());
        entity.setNextTip(parsed.nextTip());
        entity.setDailyTitle(parsed.dailyTitle());
        entity.setStreakTitle(parsed.streakTitle());
        entity.setPetMessage(parsed.petMessage());
        entity.setXpEarned(xpEarned);
        entity.setStreakIncreased(streakIncreased);
        entity.setTreeGrowthDelta(treeDelta);
        entity.setAchievementUnlocked(unlockAchievement);
        entity.setAchievementTitle(unlockAchievement && parsed.achievement() != null ? parsed.achievement().title() : null);
        entity.setAchievementDescription(unlockAchievement && parsed.achievement() != null ? parsed.achievement().description() : null);

        return dailyInsightRepository.save(entity);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private UserGamification getOrCreateGamification(UUID userId) {
        return gamificationRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
            UserGamification gam = new UserGamification();
            gam.setUser(user);
            return gamificationRepository.save(gam);
        });
    }

    private DailyInsightResponse buildResponse(LocalDate today, DailyInsight insight, UserGamification gam) {
        boolean available = insight != null;

        StreakInfo streak = new StreakInfo(
                gam.getCurrentStreak(),
                gam.getLongestStreak(),
                available && Boolean.TRUE.equals(insight.getStreakIncreased()),
                available ? insight.getStreakTitle() : null
        );

        TreeInfo tree = new TreeInfo(
                gam.getTreeGrowthPercent(),
                available && insight.getTreeGrowthDelta() != null ? insight.getTreeGrowthDelta() : 0,
                gam.getTreeCount()
        );

        XpInfo xpInfo = new XpInfo(
                gam.getXp(),
                gam.getLevel(),
                available && insight.getXpEarned() != null ? insight.getXpEarned() : 0,
                gam.getXp() % XP_PER_LEVEL,
                XP_PER_LEVEL
        );

        AchievementInfo achievement = new AchievementInfo(
                available && Boolean.TRUE.equals(insight.getAchievementUnlocked()),
                available ? insight.getAchievementTitle() : null,
                available ? insight.getAchievementDescription() : null
        );

        return new DailyInsightResponse(
                available,
                today,
                available ? insight.getMoodScore() : null,
                available ? insight.getEmotion() : null,
                available ? insight.getSummary() : null,
                available ? insight.getInsight() : null,
                available ? insight.getNextTip() : null,
                available ? insight.getDailyTitle() : null,
                available ? insight.getPetMessage() : null,
                achievement,
                streak,
                tree,
                xpInfo
        );
    }
}
