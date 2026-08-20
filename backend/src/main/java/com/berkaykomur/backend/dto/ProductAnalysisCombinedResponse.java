package com.berkaykomur.backend.dto;

public record ProductAnalysisCombinedResponse(
        ProductResponse product,
        AnalysisResult analysis
) {}
