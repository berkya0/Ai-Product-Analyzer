package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.dto.ProductAnalysisCombinedResponse;
import com.berkaykomur.backend.dto.ProductResponse;
import com.berkaykomur.backend.model.Product;
import com.berkaykomur.backend.scrapper.Scrapper;
import com.berkaykomur.backend.service.AiAnalysisService;
import com.berkaykomur.backend.service.ScrapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScrapAndAnalysisService {

    private final ScrapperService scrapperService;
    private final AiAnalysisService aiAnalysisService;

    @Transactional
    public ProductAnalysisCombinedResponse scrapAndAnalysis(String productUrl){

        ProductResponse scrappedProduct=scrapperService.executeScrapping(productUrl);
        Scrapper scrapper=scrapperService.getScrapper(productUrl);
        AnalysisResult analysisResult= aiAnalysisService.createAnalysis(scrapper,scrappedProduct.id());

        return  new ProductAnalysisCombinedResponse(scrappedProduct,analysisResult);

    }
}
