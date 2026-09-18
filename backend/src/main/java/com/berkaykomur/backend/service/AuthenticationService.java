package com.berkaykomur.backend.service;

import com.berkaykomur.backend.dto.AuthResponse;
import com.berkaykomur.backend.dto.LoginRequest;
import com.berkaykomur.backend.dto.RefreshTokenRequest;
import com.berkaykomur.backend.dto.RegisterRequest;

public interface AuthenticationService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshAccessToken(RefreshTokenRequest requestRefreshToken);
}
