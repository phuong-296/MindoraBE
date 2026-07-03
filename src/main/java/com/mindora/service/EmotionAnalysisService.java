package com.mindora.service;

import java.math.BigDecimal;

public interface EmotionAnalysisService {
    String detectEmotion(String text);
    BigDecimal getSentimentScore(String emotion);
}
