package com.berkaykomur.backend.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ScrapperResult(
        Long id,
        String name,
        String imageUrl,
        String productUrl,
        Double rating,
        BigDecimal price,
        Integer reviewCount,
        Integer ratingCount

) {
}
