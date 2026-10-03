package com.berkaykomur.backend.dto;

import lombok.Builder;

import java.util.List;

@Builder
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
