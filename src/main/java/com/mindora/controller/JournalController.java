package com.mindora.controller;

import com.mindora.dto.request.JournalRequest;
import com.mindora.dto.response.JournalResponse;
import com.mindora.dto.response.PageResponse;
import com.mindora.security.UserPrincipal;
import com.mindora.service.JournalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/journals")
@RequiredArgsConstructor
public class JournalController {

    private final JournalService journalService;

    @GetMapping
    public ResponseEntity<PageResponse<JournalResponse>> list(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = journalService.list(principal.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok(PageResponse.of(result, j -> j));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JournalResponse> get(
            @AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID id) {
        return ResponseEntity.ok(journalService.get(principal.getId(), id));
    }

    @PostMapping
    public ResponseEntity<JournalResponse> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody JournalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(journalService.create(principal.getId(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JournalResponse> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @Valid @RequestBody JournalRequest request) {
        return ResponseEntity.ok(journalService.update(principal.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID id) {
        journalService.delete(principal.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
