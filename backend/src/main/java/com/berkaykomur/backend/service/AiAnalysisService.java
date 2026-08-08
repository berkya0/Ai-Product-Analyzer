package com.berkaykomur.backend.service;

import com.berkaykomur.backend.dto.AnalysisResult;

public interface AiAnalysisService {
    AnalysisResult  createAnalysis (Long productId);
}
