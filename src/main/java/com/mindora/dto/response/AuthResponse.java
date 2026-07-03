package com.mindora.dto.response;

import java.util.UUID;

public record AuthResponse(
        String token,
        UserResponse user,
        UUID conversationId
) {}
