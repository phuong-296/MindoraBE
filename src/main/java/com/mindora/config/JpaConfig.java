package com.mindora.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Bật JPA Auditing để @CreatedDate / @LastModifiedDate hoạt động.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
