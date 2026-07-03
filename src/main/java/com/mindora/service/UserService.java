package com.mindora.service;

import com.mindora.dto.request.PreferencesRequest;
import com.mindora.dto.response.UserResponse;

import java.util.UUID;

public interface UserService {
    UserResponse getCurrentUser(UUID userId);
    UserResponse updateProfile(UUID userId, String fullName, String avatarUrl);
    PreferencesRequest updatePreferences(UUID userId, PreferencesRequest req);
}
