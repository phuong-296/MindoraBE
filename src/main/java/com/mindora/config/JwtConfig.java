package com.mindora.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Đọc cấu hình JWT từ application.properties / application.yml.
 * secret: khóa bí mật ký token (nên dài ≥ 256 bit để đảm bảo an toàn HS256).
 * expiration: thời gian sống của token tính bằng milliseconds (mặc định 86400000 = 24h).
 */
@Getter
@Configuration
public class JwtConfig {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private long expiration;   // milliseconds, mặc định 86400000 (24h)
}
