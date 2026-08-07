package com.berkaykomur.backend.ai;

import com.berkaykomur.backend.scrapper.Scrapper;

public interface AiAnalysis {
    void analyzeComments(Scrapper scrapper, String productUrl);

}
