package com.mindora.security;

import com.mindora.entity.User;
import com.mindora.repository.UserRepository;
import com.mindora.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Triển khai UserDetailsService của Spring Security.
 * JwtAuthFilter gọi loadUserByUsername() để lấy thông tin user từ DB theo email
 * rồi tạo Authentication object đặt vào SecurityContext.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User không tồn tại: " + email));

        // Lấy danh sách role của user (USER, EXPERT, ADMIN)
        List<String> roleNames = userRoleRepository.findByUserId(user.getId())
                .stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toList());

        // Chuyển sang UserPrincipal — chứa thêm UUID để controller dùng @AuthenticationPrincipal
        return UserPrincipal.from(user, roleNames);
    }
}
