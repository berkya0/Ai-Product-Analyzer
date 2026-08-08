package com.berkaykomur.backend.dto;

import com.berkaykomur.backend.model.CommentType;

public record AnalysisHighLightResult(
        String aiComments,
        CommentType commentType
) {
}
