package com.mindora.service;

import com.mindora.dto.response.ContentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContentService {
    Page<ContentResponse> list(String moodTag, String contentType, Pageable pageable);
}
