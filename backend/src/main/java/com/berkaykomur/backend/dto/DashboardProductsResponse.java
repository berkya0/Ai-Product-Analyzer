package com.berkaykomur.backend.dto;

import com.berkaykomur.backend.model.Status;

import java.time.LocalDateTime;

public record DashboardProductsResponse(

        Long id,
        String name,
        String imageUrl,
        Double aiScore,
        boolean isFollowing,
        Status status,
        LocalDateTime updatedAt

) {
}
