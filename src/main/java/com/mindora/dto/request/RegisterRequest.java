package com.mindora.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Mọi tài khoản đăng ký qua API đều nhận role "USER" (xem AuthServiceImpl.register()).
 * Tính năng đăng ký EXPERT đã bị gỡ bỏ vì không được frontend sử dụng.
 */
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
}
