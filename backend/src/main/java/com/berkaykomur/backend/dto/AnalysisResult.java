package com.berkaykomur.backend.dto;

import java.util.List;

public record AnalysisResult(
         Double aiScore,
         String summary,
         String topPositiveComment,
         String topNegativeComment,
         List<AnalysisHighLightResult> highlights,
         List<FeatureSentimentResult> featureResults
) {
}
