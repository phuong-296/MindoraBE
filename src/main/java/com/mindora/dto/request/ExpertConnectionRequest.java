package com.mindora.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ExpertConnectionRequest {

    @NotNull(message = "expertId không được để trống")
    private UUID expertId;

    private String triggerReason;   // self_request | ai_alert | expert_invite
    private String notes;
}
