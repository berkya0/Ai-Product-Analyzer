package com.berkaykomur.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record CompareResults(
        Long id,
        Double aiScore,
        String summary,
        String name,
        BigDecimal price,
        String imageUrl,
        Integer reviewCount,
        Integer ratingCount

) {
}
