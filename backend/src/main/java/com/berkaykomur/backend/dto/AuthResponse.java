package com.berkaykomur.backend.dto;

public record AuthResponse(
        Long id,
        String username,
        String accessToken,
        String refreshToken
) {}
