package com.mindora.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        String role,
        String content,
        String detectedEmotion,
        BigDecimal sentimentScore,
        Instant createdAt,
        // Bài hát Dora đã gợi ý kèm tin nhắn AI này (nếu có) — dựng lại từ song_ids đã lưu, để
        // vẫn hiển thị đúng sau khi load lại lịch sử, không chỉ lúc mới gửi trong phiên hiện tại.
        List<SongResponse> songs
) {}
