package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.ai.AiAnalysis;
import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.exception.product.ProductNotFoundException;
import com.berkaykomur.backend.model.Product;
import com.berkaykomur.backend.repository.ProductRepository;
import com.berkaykomur.backend.scrapper.Scrapper;
import com.berkaykomur.backend.service.AiAnalysisService;
import com.berkaykomur.backend.util.AnalysisSaveHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiAnalysisServiceImpl implements AiAnalysisService {

    private final AiAnalysis aiAnalysis;
    private final ProductRepository productRepository;
    private final AnalysisSaveHelper analysisSaveHelper;

    @Override
    public AnalysisResult createAnalysis(Scrapper scrapper, Long productId, Long userId) {
        log.info("Analiz süreci başlatıldı. Product ID: {}", productId);
        Product product = productRepository.findByIdAndUser_Id(productId, userId)
                .orElseThrow(() -> new ProductNotFoundException("Id'ye göre ürün bulunamadı: " + productId));

        String productUrl = product.getProductUrl();
        AnalysisResult analysisResult = aiAnalysis.analyzeComments(scrapper, productUrl);
        return analysisSaveHelper.saveAnalysisResult(product, userId, analysisResult);
    }


}
