package com.mindora.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AnalysisResponse(
        UUID id,
        String sourceType,
        BigDecimal depressionRiskScore,
        String riskLevel,
        String aiSummary,
        Boolean alertSent,
        Instant analyzedAt
) {}
