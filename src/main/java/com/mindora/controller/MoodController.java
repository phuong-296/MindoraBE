package com.mindora.controller;

import com.mindora.dto.request.MoodLogRequest;
import com.mindora.dto.response.MoodLogResponse;
import com.mindora.security.UserPrincipal;
import com.mindora.service.MoodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/moods")
@RequiredArgsConstructor
public class MoodController {

    private final MoodService moodService;

    @PostMapping
    public ResponseEntity<MoodLogResponse> log(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody MoodLogRequest request) {
        return ResponseEntity.ok(moodService.logMood(principal.getId(), request));
    }

    @GetMapping("/week")
    public ResponseEntity<List<MoodLogResponse>> week(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(moodService.getWeek(principal.getId()));
    }

    @GetMapping("/range")
    public ResponseEntity<List<MoodLogResponse>> range(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(moodService.getRange(principal.getId(), from, to));
    }
}
