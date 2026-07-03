package com.mindora.service;

import com.mindora.dto.response.ContentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ContentService {
    Page<ContentResponse> list(UUID userId, String moodTag, String contentType, Pageable pageable);
    List<ContentResponse> listSaved(UUID userId);
    void save(UUID userId, UUID contentId);
    void unsave(UUID userId, UUID contentId);
}
