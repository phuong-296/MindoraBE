package com.mindora.service;

import com.mindora.dto.request.JournalRequest;
import com.mindora.dto.response.JournalResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface JournalService {
    Page<JournalResponse> list(UUID userId, Pageable pageable);
    JournalResponse get(UUID userId, UUID id);
    JournalResponse create(UUID userId, JournalRequest request);
    JournalResponse update(UUID userId, UUID id, JournalRequest request);
    void delete(UUID userId, UUID id);
}
