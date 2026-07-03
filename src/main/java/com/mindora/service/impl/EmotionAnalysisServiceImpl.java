package com.mindora.service.impl;
import com.mindora.service.EmotionAnalysisService;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Phân tích cảm xúc đơn giản dựa trên từ khoá tiếng Việt (rule-based).
 * Có thể thay bằng model NLP thật về sau mà không đổi interface.
 */
@Service
public class EmotionAnalysisServiceImpl implements EmotionAnalysisService {

    // Thu tu uu tien: anxious → sad → angry → tired → happy
    // LinkedHashMap dam bao thu tu duyet, tranh Map.of() random order
    private static final Map<String, List<String>> EMOTION_KEYWORDS;
    static {
        EMOTION_KEYWORDS = new java.util.LinkedHashMap<>();
        EMOTION_KEYWORDS.put("anxious", List.of(
            "lo lắng", "căng thẳng", "áp lực", "hoảng loạn", "sợ hãi", "lo âu", "stress",
            "lo lang",  "cang thang",  "ap luc",  "hoang loan",  "so hai",   "lo au",
            "panic", "anxiety", "nervous", "worried"
        ));
        EMOTION_KEYWORDS.put("sad", List.of(
            "buồn", "khóc", "cô đơn", "thất vọng", "chán nản", "tuyệt vọng", "đau lòng",
            "buon",  "khoc",  "co don",  "that vong",  "chan nan",  "tuyet vong",  "dau long",
            "sad", "depressed", "lonely", "hopeless", "crying"
        ));
        EMOTION_KEYWORDS.put("angry", List.of(
            "tức giận", "bực bội", "khó chịu", "ghét", "tức", "bực", "giận dữ",
            "tuc gian",  "buc boi",  "kho chiu",  "ghet",  "tuc",  "buc",  "gian du",
            "angry", "mad", "furious", "frustrated"
        ));
        EMOTION_KEYWORDS.put("tired", List.of(
            "mệt mỏi", "kiệt sức", "buồn ngủ", "mệt", "kiệt",
            "met moi",  "kiet suc",  "buon ngu",  "met",  "kiet liet",
            "tired", "exhausted", "fatigue", "burnout"
        ));
        EMOTION_KEYWORDS.put("happy", List.of(
            "vui vẻ", "hạnh phúc", "tuyệt vời", "phấn khởi", "vui",
            "vui ve",  "hanh phuc",  "tuyet voi",  "phan khoi",
            "happy", "joyful", "excited", "great"
        ));
    }

    private static final Map<String, BigDecimal> SENTIMENT_SCORE = Map.of(
        "happy",   new BigDecimal("0.85"),
        "tired",   new BigDecimal("0.30"),
        "sad",     new BigDecimal("0.20"),
        "anxious", new BigDecimal("0.25"),
        "angry",   new BigDecimal("0.10"),
        "neutral", new BigDecimal("0.50")
    );

    public String detectEmotion(String text) {
        if (text == null) return "neutral";
        String lower = text.toLowerCase();
        for (Map.Entry<String, List<String>> entry : EMOTION_KEYWORDS.entrySet()) {
            if (entry.getValue().stream().anyMatch(lower::contains)) {
                return entry.getKey();
            }
        }
        return "neutral";
    }

    public BigDecimal getSentimentScore(String emotion) {
        return SENTIMENT_SCORE.getOrDefault(emotion, new BigDecimal("0.50"));
    }
}
