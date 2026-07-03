package com.mindora.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ExpertResponse(
        UUID id,
        String fullName,
        String avatarUrl,
        String specialization,
        String bio,
        String location,
        Integer yearsExperience,
        BigDecimal rating,
        Boolean isOnline,
        Boolean isVerified
) {}
