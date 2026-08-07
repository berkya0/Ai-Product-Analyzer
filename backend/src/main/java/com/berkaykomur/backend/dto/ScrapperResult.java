package com.berkaykomur.backend.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ScrapperResult(
        String name,
        String imageUrl,
        String productUrl,
        Double rating,
        BigDecimal price,
        Integer reviewCount,
        Integer ratingCount

) {
}
