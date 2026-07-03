package com.mindora.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu tối thiểu 6 ký tự")
    private String password;

    /**
     * Role khi đăng ký: "USER" hoặc "EXPERT".
     * Mặc định là "USER" nếu không truyền.
     * ADMIN không được phép đăng ký qua API.
     */
    @Pattern(regexp = "(?i)USER|EXPERT", message = "Role phải là 'USER' hoặc 'EXPERT'")
    private String role;
}
