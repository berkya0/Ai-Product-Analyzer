package com.berkaykomur.backend.service;

import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.scrapper.Scrapper;

public interface AiAnalysisService {
    AnalysisResult  createAnalysis (Scrapper scrapper, Long productId,boolean forceRefresh);
}
