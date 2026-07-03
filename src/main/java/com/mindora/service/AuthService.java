package com.mindora.service;

import com.mindora.dto.request.LoginRequest;
import com.mindora.dto.request.RegisterRequest;
import com.mindora.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
