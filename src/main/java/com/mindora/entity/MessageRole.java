package com.mindora.entity;

/**
 * Vai trò của tin nhắn. Lưu dạng STRING.
 * Frontend gửi "user"/"ai" -> service chuẩn hoá bằng toUpperCase() trước khi valueOf().
 */
public enum MessageRole {
    USER,
    AI
}
