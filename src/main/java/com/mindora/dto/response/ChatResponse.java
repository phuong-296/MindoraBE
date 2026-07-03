package com.mindora.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

/** Response trả về khi user gửi tin nhắn — bao gồm cả phản hồi AI */
@Getter
@AllArgsConstructor
public class ChatResponse {
    private MessageResponse userMessage;
    private MessageResponse aiResponse;
    private List<String>    retrievedSources; // Tên tài liệu đã dùng (transparency)
}
