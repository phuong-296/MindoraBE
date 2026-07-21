package com.mindora.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

/**
 * Response trả về khi user gửi tin nhắn — bao gồm cả phản hồi AI.
 * reply/mood/songs là hợp đồng chính của tính năng chat:
 *  - reply: văn bản Dora trả lời (giống aiResponse.getContent(), thêm ở đây cho tiện FE đọc trực tiếp)
 *  - mood:  một trong happy | calm | sad | stress | sleep | energy — do Gemini phân tích toàn bộ hội thoại
 *  - songs: bài hát THẬT lấy từ content_library theo đúng mood — AI không tự bịa bài hát/link
 */
@Getter
@AllArgsConstructor
public class ChatResponse {
    private MessageResponse   userMessage;
    private MessageResponse   aiResponse;
    private List<String>      retrievedSources; // Tên tài liệu đã dùng (transparency)
    private String            reply;
    private String            mood;
    private List<SongResponse> songs;
}
