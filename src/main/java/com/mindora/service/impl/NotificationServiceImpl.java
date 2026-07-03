package com.mindora.service.impl;
import com.mindora.service.NotificationService;

import com.mindora.dto.response.NotificationResponse;
import com.mindora.entity.Notification;
import com.mindora.exception.ResourceNotFoundException;
import com.mindora.repository.NotificationRepository;
import com.mindora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Quản lý thông báo trong ứng dụng.
 * create() được gọi nội bộ từ các service khác (ExpertService, AnalysisService).
 * markAsRead() kiểm tra owner để tránh user đánh dấu thông báo của người khác.
 * markAllAsRead() dùng bulk UPDATE thay vì load từng entity để tối ưu hiệu năng.
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void create(UUID userId, String type, String title, String message) {
        Notification n = new Notification();
        n.setUser(userRepository.getReferenceById(userId));
        n.setType(type);
        n.setTitle(title);
        n.setMessage(message);
        notificationRepository.save(n);
    }

    /** Cảnh báo sức khỏe tâm thần khẩn cấp — gọi từ AnalysisService khi riskLevel = critical. */
    @Transactional
    public void sendAlert(UUID userId, String title, String message) {
        create(userId, "expert_alert", title, message);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> list(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    /** Đếm số thông báo chưa đọc — dùng để hiển thị badge trên giao diện. */
    @Transactional(readOnly = true)
    public long unreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(UUID userId, UUID notificationId) {
        // filter theo userId để ngăn user A đánh dấu đọc thông báo của user B
        Notification n = notificationRepository.findById(notificationId)
                .filter(x -> x.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo"));
        n.setIsRead(true);
        notificationRepository.save(n);
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        // Bulk update — hiệu quả hơn load từng entity rồi set
        notificationRepository.markAllAsReadByUserId(userId);
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(), n.getType(), n.getTitle(), n.getMessage(),
                n.getIsRead(), n.getCreatedAt());
    }
}
