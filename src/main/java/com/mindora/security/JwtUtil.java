package com.mindora.security;

import com.mindora.config.JwtConfig;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Tiện ích tạo, giải mã và xác thực JWT.
 * Dùng thuật toán HS256 (HMAC-SHA256) với secret key từ JwtConfig.
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtConfig jwtConfig;

    /** Tạo JWT token với email làm subject, hết hạn sau thời gian cấu hình. */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** Trích xuất email (subject) từ token đã được ký. */
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey()).build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    /** Kiểm tra token có hợp lệ không (chữ ký đúng và chưa hết hạn). */
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** Chuyển secret string thành HMAC key dùng để ký / xác minh. */
    private Key getSignKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
