package com.mindora.controller;

import com.mindora.dto.response.ExpertResponse;
import com.mindora.dto.response.PageResponse;
import com.mindora.service.ExpertService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/experts")
@RequiredArgsConstructor
public class ExpertController {

    private final ExpertService expertService;

    @GetMapping
    public ResponseEntity<PageResponse<ExpertResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = expertService.listVerifiedExperts(PageRequest.of(page, size));
        return ResponseEntity.ok(PageResponse.of(result, e -> e));
    }

    @GetMapping("/{expertId}")
    public ResponseEntity<ExpertResponse> get(@PathVariable UUID expertId) {
        return ResponseEntity.ok(expertService.getExpert(expertId));
    }
}
