package com.mindora.config;

import com.mindora.security.JwtAuthFilter;
import com.mindora.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Cấu hình bảo mật trung tâm của ứng dụng.
 * - Tắt CSRF (API stateless dùng JWT, không dùng session/cookie).
 * - Không tạo session phía server (STATELESS).
 * - Đặt JwtAuthFilter trước UsernamePasswordAuthenticationFilter để xác thực token trước.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Dùng CORS config từ CorsConfig bean
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            // Tắt CSRF vì API dùng JWT thay vì session
            .csrf(AbstractHttpConfigurer::disable)
            // Không lưu session — mỗi request phải tự mang JWT
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Cho phép đăng ký / đăng nhập mà không cần token
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/error").permitAll()
                // Swagger UI — không cần auth
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/v3/api-docs.yaml"
                ).permitAll()
                // Endpoint chỉ dành cho EXPERT đặt TRƯỚC rule chung của /api/experts/**
                .requestMatchers("/api/expert-connections/*/status").hasRole("EXPERT")
                .requestMatchers("/api/experts/**").hasAnyRole("USER", "EXPERT", "ADMIN")
                // Tất cả endpoint còn lại đều yêu cầu đăng nhập
                .anyRequest().authenticated()
            )
            // Chèn JwtAuthFilter vào trước filter xác thực mặc định của Spring
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /** Cung cấp AuthenticationManager để AuthService gọi authenticate() khi đăng nhập. */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /** BCrypt để hash mật khẩu — cost factor mặc định là 10. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
