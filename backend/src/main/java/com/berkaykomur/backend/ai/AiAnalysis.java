package com.berkaykomur.backend.ai;

import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.scrapper.Scrapper;

public interface AiAnalysis {
    AnalysisResult analyzeComments(Scrapper scrapper, String productUrl);

}
