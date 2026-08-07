package com.berkaykomur.backend.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ScrapperResponse(
        String name,
        String imageUrl,
        String productUrl,
        Double rating,
        BigDecimal price,
        Integer reviewCount,
        Integer ratingCount

) {
}
