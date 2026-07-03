package com.mindora.controller;

import com.mindora.dto.response.ContentResponse;
import com.mindora.dto.response.PageResponse;
import com.mindora.security.UserPrincipal;
import com.mindora.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @GetMapping
    public ResponseEntity<PageResponse<ContentResponse>> list(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String moodTag,
            @RequestParam(required = false) String contentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = contentService.list(
                principal.getId(), moodTag, contentType, PageRequest.of(page, size));
        return ResponseEntity.ok(PageResponse.of(result, c -> c));
    }

    @GetMapping("/saved")
    public ResponseEntity<List<ContentResponse>> saved(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(contentService.listSaved(principal.getId()));
    }

    @PostMapping("/{contentId}/save")
    public ResponseEntity<Void> save(
            @AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID contentId) {
        contentService.save(principal.getId(), contentId);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/{contentId}/save")
    public ResponseEntity<Void> unsave(
            @AuthenticationPrincipal UserPrincipal principal, @PathVariable UUID contentId) {
        contentService.unsave(principal.getId(), contentId);
        return ResponseEntity.noContent().build();
    }
}
