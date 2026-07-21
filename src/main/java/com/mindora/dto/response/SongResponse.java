package com.mindora.dto.response;

import java.util.UUID;

/**
 * Bài hát THẬT lấy từ content_library, gắn vào phản hồi chat theo mood mà Gemini xác định.
 * Backend tự truy vấn DB — AI không tự bịa bài hát/ca sĩ/link nào.
 */
public record SongResponse(
        UUID id,
        String title,
        String artist,
        String youtubeId,
        String moodTag
) {}
