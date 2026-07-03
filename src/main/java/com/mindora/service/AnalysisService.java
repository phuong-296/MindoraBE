package com.mindora.service;

import com.mindora.dto.response.AnalysisResponse;

import java.util.List;
import java.util.UUID;

public interface AnalysisService {
    AnalysisResponse runAnalysis(UUID userId);
    AnalysisResponse getLatest(UUID userId);
    List<AnalysisResponse> history(UUID userId);
}
