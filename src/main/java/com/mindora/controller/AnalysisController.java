package com.mindora.controller;

import com.mindora.dto.response.AnalysisResponse;
import com.mindora.security.UserPrincipal;
import com.mindora.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping("/run")
    public ResponseEntity<AnalysisResponse> run(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(analysisService.runAnalysis(principal.getId()));
    }

    @GetMapping("/latest")
    public ResponseEntity<AnalysisResponse> latest(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(analysisService.getLatest(principal.getId()));
    }

    @GetMapping("/history")
    public ResponseEntity<List<AnalysisResponse>> history(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(analysisService.history(principal.getId()));
    }
}
