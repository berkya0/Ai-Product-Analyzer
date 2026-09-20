package com.berkaykomur.backend.dto;

public record SiteResponse(
        Long id,
        String siteName,
        String siteUrl,
        String username
) {
}
