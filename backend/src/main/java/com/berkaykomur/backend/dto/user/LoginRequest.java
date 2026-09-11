package com.berkaykomur.backend.dto.user;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Kullanıcı adınızı girin")
        String username,

        @NotBlank(message = "Lütfen şifrenizi girin")
        String password
) {
}
