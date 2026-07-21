package com.mindora.service.impl;
import com.mindora.service.UserService;

import com.mindora.dto.response.UserResponse;
import com.mindora.entity.User;
import com.mindora.exception.ResourceNotFoundException;
import com.mindora.repository.UserRepository;
import com.mindora.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

/**
 * Quản lý thông tin cá nhân của user.
 * updateProfile() chỉ cập nhật trường nào client gửi lên (null = không đổi).
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
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

    @Override
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
}
