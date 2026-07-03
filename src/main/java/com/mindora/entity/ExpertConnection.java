package com.mindora.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

/**
 * Yêu cầu kết nối giữa user và chuyên gia tư vấn.
 * Luồng trạng thái: pending → accepted / rejected; accepted → closed (kết thúc tư vấn).
 * triggerReason ghi lại nguyên nhân kết nối để phân tích: tự nguyện, cảnh báo AI, hay chuyên gia mời.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "expert_connections")
public class ExpertConnection extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_id", nullable = false)
    private Expert expert;

    @Column(nullable = false, length = 50)
    private String status;          // pending | accepted | rejected | closed

    @Column(name = "trigger_reason", length = 50)
    private String triggerReason;   // self_request | ai_alert | expert_invite

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "requested_at")
    private Instant requestedAt = Instant.now();

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
