package com.mindora.controller;

import com.mindora.dto.response.ContentResponse;
import com.mindora.dto.response.PageResponse;
import com.mindora.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @GetMapping
    public ResponseEntity<PageResponse<ContentResponse>> list(
            @RequestParam(required = false) String moodTag,
            @RequestParam(required = false) String contentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = contentService.list(moodTag, contentType, PageRequest.of(page, size));
        return ResponseEntity.ok(PageResponse.of(result, c -> c));
    }
}
