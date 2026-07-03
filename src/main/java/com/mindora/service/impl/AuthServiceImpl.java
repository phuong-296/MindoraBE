package com.mindora.service.impl;
import com.mindora.service.AuthService;

import com.mindora.dto.request.LoginRequest;
import com.mindora.dto.request.RegisterRequest;
import com.mindora.dto.response.AuthResponse;
import com.mindora.dto.response.UserResponse;
import com.mindora.entity.*;
import com.mindora.exception.DuplicateResourceException;
import com.mindora.exception.ResourceNotFoundException;
import com.mindora.repository.*;
import com.mindora.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Xử lý đăng ký và đăng nhập.
 * register(): tạo user, gán role, tạo preferences mặc định và conversation đầu tiên trong 1 transaction.
 * login(): dùng AuthenticationManager để xác thực (BCrypt so sánh password), trả về JWT + conversation gần nhất.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserPreferencesRepository preferencesRepository;
    private final AiConversationRepository conversationRepository;
    private final ExpertRepository expertRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email đã tồn tại trong hệ thống");
        }

        // 1. Tạo user và hash mật khẩu
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        // 2. Xác định role (mặc định USER nếu không truyền)
        String requestedRole = (request.getRole() != null) ? request.getRole().toUpperCase() : "USER";
        List<String> assignedRoles = new java.util.ArrayList<>();

        // Luôn gán role USER cho mọi tài khoản
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role USER không tồn tại"));
        UserRole userRoleEntity = new UserRole();
        userRoleEntity.setUser(user);
        userRoleEntity.setRole(userRole);
        userRoleRepository.save(userRoleEntity);
        assignedRoles.add("USER");

        // Nếu đăng ký là EXPERT → gán thêm role EXPERT + tạo Expert profile (chưa verified)
        if ("EXPERT".equals(requestedRole)) {
            Role expertRole = roleRepository.findByName("EXPERT")
                    .orElseThrow(() -> new ResourceNotFoundException("Role EXPERT không tồn tại"));
            UserRole expertRoleEntity = new UserRole();
            expertRoleEntity.setUser(user);
            expertRoleEntity.setRole(expertRole);
            userRoleRepository.save(expertRoleEntity);
            assignedRoles.add("EXPERT");

            // Tạo Expert profile mặc định, admin sẽ verify sau
            Expert expert = new Expert();
            expert.setUser(user);
            expert.setIsVerified(false);
            expert.setIsOnline(false);
            expertRepository.save(expert);
        }

        // 3. Tạo preferences mặc định (language=vi, notification=daily...)
        UserPreferences prefs = new UserPreferences();
        prefs.setUser(user);
        preferencesRepository.save(prefs);

        // 4. Tạo conversation đầu tiên để frontend có thể dùng ngay sau đăng ký
        AiConversation conv = new AiConversation();
        conv.setUser(user);
        conv.setTitle("Cuộc trò chuyện đầu tiên");
        conversationRepository.save(conv);

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, toUserResponse(user, assignedRoles), conv.getId());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        // AuthenticationManager kiểm tra email + password qua CustomUserDetailsService và BCrypt
        // Ném BadCredentialsException nếu sai → GlobalExceptionHandler trả 401
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User không tồn tại"));

        // Lấy conversation gần nhất (chưa archive); nếu không có thì tạo mới
        AiConversation latestConv = conversationRepository
                .findFirstByUserIdAndIsArchivedFalseOrderByUpdatedAtDesc(user.getId())
                .orElseGet(() -> {
                    AiConversation conv = new AiConversation();
                    conv.setUser(user);
                    conv.setTitle("Cuộc trò chuyện");
                    return conversationRepository.save(conv);
                });

        List<String> roles = userRoleRepository.findByUserId(user.getId())
                .stream().map(ur -> ur.getRole().getName()).collect(Collectors.toList());

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, toUserResponse(user, roles), latestConv.getId());
    }

    /** Chuyển User entity sang DTO để trả về client, kèm danh sách role. */
    private UserResponse toUserResponse(User user, List<String> roles) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getIsActive(),
                roles,
                user.getCreatedAt()
        );
    }
}
