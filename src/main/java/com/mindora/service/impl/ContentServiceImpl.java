package com.mindora.service.impl;
import com.mindora.service.ContentService;

import com.mindora.dto.response.ContentResponse;
import com.mindora.entity.ContentLibrary;
import com.mindora.repository.ContentLibraryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Quản lý thư viện nội dung chữa lành (nhạc, podcast...).
 * list() hỗ trợ lọc kết hợp theo moodTag và contentType.
 * (Tính năng lưu/bookmark nội dung — UserSavedContent — đã bị gỡ bỏ vì không được frontend sử dụng.)
 */
@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final ContentLibraryRepository contentRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ContentResponse> list(String moodTag, String contentType, Pageable pageable) {
        Page<ContentLibrary> page;
        boolean hasMood = moodTag != null && !moodTag.isBlank();
        boolean hasType = contentType != null && !contentType.isBlank();

        // Chọn query phù hợp dựa trên bộ lọc client truyền lên
        if (hasMood && hasType) {
            page = contentRepository.findByIsActiveTrueAndMoodTagAndContentType(moodTag, contentType, pageable);
        } else if (hasMood) {
            page = contentRepository.findByIsActiveTrueAndMoodTag(moodTag, pageable);
        } else if (hasType) {
            page = contentRepository.findByIsActiveTrueAndContentType(contentType, pageable);
        } else {
            page = contentRepository.findByIsActiveTrue(pageable);
        }

        return page.map(this::toResponse);
    }

    private ContentResponse toResponse(ContentLibrary c) {
        return new ContentResponse(
                c.getId(), c.getTitle(), c.getContentType(), c.getDescription(),
                c.getThumbnailUrl(), c.getContentUrl(), c.getSpotifyUrl(), c.getYoutubeId(), c.getMoodTag(),
                c.getDurationMinutes());
    }
}
