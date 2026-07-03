package com.mindora.service;

import com.mindora.dto.request.ExpertConnectionRequest;
import com.mindora.dto.response.ConnectionResponse;
import com.mindora.dto.response.ExpertResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ExpertService {
    Page<ExpertResponse> listVerifiedExperts(Pageable pageable);
    ExpertResponse getExpert(UUID expertId);
    ConnectionResponse requestConnection(UUID userId, ExpertConnectionRequest req);
    List<ConnectionResponse> myConnections(UUID userId);
    List<ConnectionResponse> incomingConnections(UUID expertUserId);
    ConnectionResponse updateStatus(UUID expertUserId, UUID connectionId, String status);
}
