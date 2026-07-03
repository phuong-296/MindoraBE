package com.mindora.service.impl;
import com.mindora.service.UserService;

import com.mindora.dto.request.PreferencesRequest;
import com.mindora.dto.response.UserResponse;
import com.mindora.entity.User;
import com.mindora.entity.UserPreferences;
import com.mindora.exception.ResourceNotFoundException;
import com.mindora.repository.UserPreferencesRepository;
import com.mindora.repository.UserRepository;
import com.mindora.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

/**
 * Quản lý thông tin cá nhân và tùy chọn của user.
 * updateProfile() chỉ cập nhật trường nào client gửi lên (null = không đổi).
 * updatePreferences() hoạt động như PATCH — chỉ ghi đè trường không null.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserPreferencesRepository preferencesRepository;

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại"));

        List<String> roles = userRoleRepository.findByUserId(userId)
                .stream().map(ur -> ur.getRole().getName()).collect(Collectors.toList());

        return new UserResponse(
                user.getId(), user.getFullName(), user.getEmail(),
                user.getAvatarUrl(), user.getIsActive(), roles, user.getCreatedAt());
    }

    @Transactional
    public UserResponse updateProfile(UUID userId, String fullName, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại"));
        // Null check: chỉ cập nhật trường client gửi lên, giữ nguyên trường còn lại
        if (fullName != null)  user.setFullName(fullName);
        if (avatarUrl != null) user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
        return getCurrentUser(userId);
    }

    @Transactional
    public PreferencesRequest updatePreferences(UUID userId, PreferencesRequest req) {
        // Tìm preferences hiện có, nếu chưa có (edge case) thì tạo mới
        UserPreferences prefs = preferencesRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserPreferences p = new UserPreferences();
                    p.setUser(userRepository.getReferenceById(userId));
                    return p;
                });

        // Chỉ ghi đè field nào client gửi (partial update / PATCH style)
        if (req.getLanguage() != null)              prefs.setLanguage(req.getLanguage());
        if (req.getFavoriteMusicGenres() != null)   prefs.setFavoriteMusicGenres(req.getFavoriteMusicGenres());
        if (req.getNotificationFrequency() != null) prefs.setNotificationFrequency(req.getNotificationFrequency());
        if (req.getDarkMode() != null)              prefs.setDarkMode(req.getDarkMode());
        if (req.getEmailNotifications() != null)    prefs.setEmailNotifications(req.getEmailNotifications());
        if (req.getPushNotifications() != null)     prefs.setPushNotifications(req.getPushNotifications());

        preferencesRepository.save(prefs);
        return req;
    }
}
