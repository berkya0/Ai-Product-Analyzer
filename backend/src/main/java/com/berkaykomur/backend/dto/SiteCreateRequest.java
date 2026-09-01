package com.berkaykomur.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record SiteCreateRequest(
        @NotBlank(message = "Site adı boş olamaz")
        String siteName,
        @NotBlank(message = "Site URL boş olamaz")
        String siteUrl,
        @NotBlank(message = "Kullanıcı adı boş olamaz")
        String username,
        @NotBlank(message = "Şifre boş olamaz")
        String appPassword
) {}