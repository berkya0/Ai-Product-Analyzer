package com.berkaykomur.backend.dto.user;

public record AuthResponse(
        Long id,
        String username,
        String token
) {}
