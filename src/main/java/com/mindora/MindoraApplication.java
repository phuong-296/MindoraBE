package com.mindora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Điểm khởi động của ứng dụng Mindora — hệ thống hỗ trợ sức khỏe tâm thần.
 * @SpringBootApplication kích hoạt tự động cấu hình, quét component và cấu hình Spring Boot.
 */
@SpringBootApplication
public class MindoraApplication {
    public static void main(String[] args) {
        SpringApplication.run(MindoraApplication.class, args);
    }
}
