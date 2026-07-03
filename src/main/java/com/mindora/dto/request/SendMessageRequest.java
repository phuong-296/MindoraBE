package com.mindora.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageRequest {

    @NotBlank(message = "Vai trò không được để trống")
    @Pattern(regexp = "(?i)user|ai", message = "Vai trò phải là 'user' hoặc 'ai'")
    private String role;     // user | ai (không phân biệt hoa thường)

    @NotBlank(message = "Nội dung không được để trống")
    private String content;
}
