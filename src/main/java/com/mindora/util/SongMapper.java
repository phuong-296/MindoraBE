package com.mindora.util;

import com.mindora.dto.response.SongResponse;
import com.mindora.entity.ContentLibrary;
import com.mindora.repository.ContentLibraryRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Chuyển đổi qua lại giữa danh sách bài hát (ContentLibrary) và chuỗi song_ids lưu trên Message.
 * Dùng chung giữa RagServiceImpl (lúc tạo tin nhắn AI, lưu lại đúng những bài đã gợi ý) và
 * ConversationServiceImpl (lúc load lại lịch sử) — để thẻ bài hát + link "Khám phá thêm" trong
 * Chat vẫn hiển thị đúng sau khi F5 hoặc chuyển hội thoại, thay vì chỉ tồn tại tạm trong state
 * React của phiên chat hiện tại.
 */
public final class SongMapper {

    private SongMapper() {}

    /** Ghép danh sách ContentLibrary thành chuỗi id cách nhau bởi dấu phẩy để lưu vào Message.songIds. */
    public static String toIdString(List<ContentLibrary> songs) {
        if (songs == null || songs.isEmpty()) return null;
        return songs.stream().map(c -> c.getId().toString()).collect(Collectors.joining(","));
    }

    /**
     * Parse chuỗi songIds đã lưu + tra lại content_library để dựng lại ĐÚNG danh sách bài hát
     * ban đầu (giữ nguyên thứ tự đã lưu — findAllById không đảm bảo thứ tự nên phải tự sắp lại).
     * Trả về danh sách rỗng nếu songIds null/rỗng hoặc bài hát đã bị xoá khỏi content_library.
     */
    public static List<SongResponse> resolve(String songIds, ContentLibraryRepository contentRepo) {
        if (songIds == null || songIds.isBlank()) return List.of();

        List<UUID> ids;
        try {
            ids = Arrays.stream(songIds.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .map(UUID::fromString)
                    .toList();
        } catch (IllegalArgumentException e) {
            return List.of();
        }
        if (ids.isEmpty()) return List.of();

        List<ContentLibrary> found = contentRepo.findAllById(ids);
        return ids.stream()
                .map(id -> found.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null))
                .filter(Objects::nonNull)
                .map(SongMapper::toSongResponse)
                .collect(Collectors.toList());
    }

    private static SongResponse toSongResponse(ContentLibrary c) {
        String title = c.getTitle();
        String artist = "Nhạc Việt";
        int dash = title.indexOf('—');
        if (dash >= 0) {
            artist = title.substring(dash + 1).trim();
            title = title.substring(0, dash).trim();
        }
        return new SongResponse(c.getId(), title, artist, c.getYoutubeId(), c.getMoodTag());
    }
}
