package com.mindora.controller;

import com.mindora.dto.request.ExpertConnectionRequest;
import com.mindora.dto.response.ConnectionResponse;
import com.mindora.security.UserPrincipal;
import com.mindora.service.ExpertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/expert-connections")
@RequiredArgsConstructor
public class ExpertConnectionController {

    private final ExpertService expertService;

    /** User gửi yêu cầu kết nối tới chuyên gia. */
    @PostMapping
    public ResponseEntity<ConnectionResponse> request(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ExpertConnectionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(expertService.requestConnection(principal.getId(), req));
    }

    /** Danh sách yêu cầu kết nối của chính user. */
    @GetMapping("/me")
    public ResponseEntity<List<ConnectionResponse>> myConnections(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(expertService.myConnections(principal.getId()));
    }

    /** Danh sách yêu cầu gửi tới chuyên gia hiện tại. */
    @GetMapping("/incoming")
    public ResponseEntity<List<ConnectionResponse>> incoming(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(expertService.incomingConnections(principal.getId()));
    }

    /** Chuyên gia cập nhật trạng thái (chỉ ROLE_EXPERT — xem SecurityConfig). */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ConnectionResponse> updateStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(
                expertService.updateStatus(principal.getId(), id, body.get("status")));
    }
}
