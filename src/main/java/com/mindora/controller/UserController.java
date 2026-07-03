package com.mindora.controller;

import com.mindora.dto.request.PreferencesRequest;
import com.mindora.dto.response.UserResponse;
import com.mindora.security.UserPrincipal;
import com.mindora.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userService.getCurrentUser(principal.getId()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(userService.updateProfile(
                principal.getId(), body.get("fullName"), body.get("avatarUrl")));
    }

    @PutMapping("/me/preferences")
    public ResponseEntity<PreferencesRequest> updatePreferences(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody PreferencesRequest request) {
        return ResponseEntity.ok(userService.updatePreferences(principal.getId(), request));
    }
}
