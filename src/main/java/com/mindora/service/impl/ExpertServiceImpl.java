package com.mindora.service.impl;
import com.mindora.service.ExpertService;
import com.mindora.service.NotificationService;

import com.mindora.dto.request.ExpertConnectionRequest;
import com.mindora.dto.response.ConnectionResponse;
import com.mindora.dto.response.ExpertResponse;
import com.mindora.entity.Expert;
import com.mindora.entity.ExpertConnection;
import com.mindora.exception.ResourceNotFoundException;
import com.mindora.exception.UnauthorizedException;
import com.mindora.repository.ExpertConnectionRepository;
import com.mindora.repository.ExpertRepository;
import com.mindora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Quản lý chuyên gia tư vấn và yêu cầu kết nối.
 * requestConnection(): user gửi yêu cầu → hệ thống báo chuyên gia qua notification.
 * updateStatus(): chỉ chuyên gia sở hữu connection đó mới được cập nhật trạng thái.
 */
@Service
@RequiredArgsConstructor
public class ExpertServiceImpl implements ExpertService {

    private final ExpertRepository expertRepository;
    private final ExpertConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // Danh sách trạng thái hợp lệ để validate input trước khi update
    private static final Set<String> VALID_STATUS = Set.of("pending", "accepted", "rejected", "closed");

    @Transactional(readOnly = true)
    public Page<ExpertResponse> listVerifiedExperts(Pageable pageable) {
        return expertRepository.findByIsVerifiedTrue(pageable).map(this::toExpertResponse);
    }

    @Transactional(readOnly = true)
    public ExpertResponse getExpert(UUID expertId) {
        Expert expert = expertRepository.findById(expertId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyên gia"));
        return toExpertResponse(expert);
    }

    /** User gửi yêu cầu kết nối tới chuyên gia. */
    @Transactional
    public ConnectionResponse requestConnection(UUID userId, ExpertConnectionRequest req) {
        Expert expert = expertRepository.findById(req.getExpertId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyên gia"));

        ExpertConnection conn = new ExpertConnection();
        conn.setUser(userRepository.getReferenceById(userId));
        conn.setExpert(expert);
        conn.setStatus("pending");
        conn.setTriggerReason(req.getTriggerReason() != null ? req.getTriggerReason() : "self_request");
        conn.setNotes(req.getNotes());
        connectionRepository.save(conn);

        // Báo cho chuyên gia có yêu cầu mới
        notificationService.create(expert.getUser().getId(), "system",
                "Yêu cầu kết nối mới", "Bạn có một yêu cầu kết nối mới từ người dùng.");

        return toConnectionResponse(conn);
    }

    @Transactional(readOnly = true)
    public List<ConnectionResponse> myConnections(UUID userId) {
        return connectionRepository.findByUserIdOrderByRequestedAtDesc(userId)
                .stream().map(this::toConnectionResponse).toList();
    }

    /** Danh sách yêu cầu gửi tới chuyên gia hiện tại (theo userId của chuyên gia). */
    @Transactional(readOnly = true)
    public List<ConnectionResponse> incomingConnections(UUID expertUserId) {
        Expert expert = expertRepository.findByUserId(expertUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không phải chuyên gia"));
        return connectionRepository.findByExpertIdOrderByRequestedAtDesc(expert.getId())
                .stream().map(this::toConnectionResponse).toList();
    }

    /** Chuyên gia cập nhật trạng thái 1 yêu cầu kết nối. */
    @Transactional
    public ConnectionResponse updateStatus(UUID expertUserId, UUID connectionId, String status) {
        if (!VALID_STATUS.contains(status)) {
            throw new UnauthorizedException("Trạng thái không hợp lệ: " + status);
        }
        ExpertConnection conn = connectionRepository.findByIdAndExpertUserId(connectionId, expertUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu kết nối"));
        conn.setStatus(status);
        connectionRepository.save(conn);

        notificationService.create(conn.getUser().getId(), "system",
                "Cập nhật yêu cầu kết nối", "Chuyên gia đã " + status + " yêu cầu của bạn.");

        return toConnectionResponse(conn);
    }

    private ExpertResponse toExpertResponse(Expert e) {
        var u = e.getUser();
        return new ExpertResponse(
                e.getId(), u.getFullName(), u.getAvatarUrl(),
                e.getSpecialization(), e.getBio(), e.getLocation(),
                e.getYearsExperience(), e.getRating(), e.getIsOnline(), e.getIsVerified());
    }

    private ConnectionResponse toConnectionResponse(ExpertConnection c) {
        return new ConnectionResponse(
                c.getId(),
                c.getExpert().getId(),
                c.getExpert().getUser().getFullName(),
                c.getUser().getId(),
                c.getUser().getFullName(),
                c.getStatus(),
                c.getTriggerReason(),
                c.getNotes(),
                c.getRequestedAt());
    }
}
