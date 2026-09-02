package com.berkaykomur.backend.dto;

import jakarta.persistence.Column;

public record SiteResponse(
        String siteName,
        String siteUrl,
        String username
) {
}
