package com.berkaykomur.backend.dto;

public record DashboardResponse(
        long totalAnalysis,
        long successfulAnalysis,
        long failedAnalysis,
        long totalFollowedAnalysis
) {
}
