package com.mindora.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        String avatarUrl,
        Boolean isActive,
        List<String> roles,
        Instant createdAt
) {}
