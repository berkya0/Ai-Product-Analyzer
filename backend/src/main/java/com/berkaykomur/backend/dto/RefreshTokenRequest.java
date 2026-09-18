package com.berkaykomur.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "refresh token girilmedi")
        String refreshToken
) {
}
