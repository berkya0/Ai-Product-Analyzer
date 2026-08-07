package com.berkaykomur.backend.dto;

import com.berkaykomur.backend.model.LoveType;

public record FeatureSentimentResult(
        String featureName,
        int percentage,
        LoveType loveType
) {
}
