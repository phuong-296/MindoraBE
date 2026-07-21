package com.mindora.dto.response;

import java.util.List;

/**
 * Phản hồi chat khi CHƯA đăng nhập (guest) — không có id tin nhắn/conversation vì không có gì
 * được lưu vào DB. Vẫn có songs (bài hát THẬT từ content_library, giống chat có tài khoản) —
 * khách vẫn được nghe nhạc Dora gợi ý, chỉ là không có lịch sử trò chuyện nào được lưu lại.
 */
public record GuestChatResponse(String reply, String mood, List<SongResponse> songs) {}
