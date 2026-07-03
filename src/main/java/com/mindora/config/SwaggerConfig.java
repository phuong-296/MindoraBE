package com.mindora.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình Swagger / OpenAPI 3.
 * Sau khi chạy, truy cập http://localhost:8080/swagger-ui/index.html để xem tài liệu API.
 * Thêm scheme "bearerAuth" để Swagger UI cho phép nhập JWT token và gọi các endpoint cần xác thực.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Mindora AI API")
                        .description("API hệ thống hỗ trợ sức khỏe tâm thần Mindora AI")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Mindora Team")
                                .email("mindora@example.com")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
