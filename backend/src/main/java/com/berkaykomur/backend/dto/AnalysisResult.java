package com.berkaykomur.backend.dto;

import java.util.List;

public record AnalysisResult(
         Long id,
         Double aiScore,
         String summary,
         String topPositiveComment,
         String topNegativeComment,
         List<AnalysisHighLightResult> highlights,
         List<FeatureSentimentResult> featureResults

) {
}
