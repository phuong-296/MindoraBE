package com.mindora.service.impl;
import com.mindora.service.ContentService;

import com.mindora.dto.response.ContentResponse;
import com.mindora.entity.ContentLibrary;
import com.mindora.entity.UserSavedContent;
import com.mindora.exception.DuplicateResourceException;
import com.mindora.exception.ResourceNotFoundException;
import com.mindora.repository.ContentLibraryRepository;
import com.mindora.repository.UserRepository;
import com.mindora.repository.UserSavedContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Quản lý thư viện nội dung chữa lành và danh sách bookmark của user.
 * list() hỗ trợ lọc kết hợp theo moodTag và contentType, đồng thời đánh dấu isSaved.
 * isSaved được tính bằng cách load tất cả savedIds của user vào Set trước, tránh N+1 query.
 */
@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final ContentLibraryRepository contentRepository;
    private final UserSavedContentRepository savedContentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<ContentResponse> list(UUID userId, String moodTag, String contentType, Pageable pageable) {
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

        // Load toàn bộ savedIds một lần vào Set để O(1) lookup khi map từng content
        Set<UUID> savedIds = savedContentRepository.findByUserIdOrderBySavedAtDesc(userId)
                .stream().map(s -> s.getContent().getId()).collect(Collectors.toSet());

        return page.map(c -> toResponse(c, savedIds.contains(c.getId())));
    }

    @Transactional(readOnly = true)
    public java.util.List<ContentResponse> listSaved(UUID userId) {
        return savedContentRepository.findByUserIdOrderBySavedAtDesc(userId)
                .stream().map(s -> toResponse(s.getContent(), true)).toList();
    }

    @Transactional
    public void save(UUID userId, UUID contentId) {
        if (!contentRepository.existsById(contentId)) {
            throw new ResourceNotFoundException("Không tìm thấy nội dung");
        }
        // Kiểm tra trùng trước để trả lỗi rõ ràng thay vì để DB throw constraint violation
        if (savedContentRepository.existsByUserIdAndContentId(userId, contentId)) {
            throw new DuplicateResourceException("Nội dung đã được lưu");
        }
        UserSavedContent usc = new UserSavedContent();
        usc.setUser(userRepository.getReferenceById(userId));
        usc.setContent(contentRepository.getReferenceById(contentId));
        savedContentRepository.save(usc);
    }

    @Transactional
    public void unsave(UUID userId, UUID contentId) {
        savedContentRepository.deleteByUserIdAndContentId(userId, contentId);
    }

    private ContentResponse toResponse(ContentLibrary c, boolean isSaved) {
        return new ContentResponse(
                c.getId(), c.getTitle(), c.getContentType(), c.getDescription(),
                c.getThumbnailUrl(), c.getContentUrl(), c.getMoodTag(),
                c.getDurationMinutes(), isSaved);
    }
}
