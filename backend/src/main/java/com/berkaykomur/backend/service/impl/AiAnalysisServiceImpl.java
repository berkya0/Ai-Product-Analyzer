package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.ai.AiAnalysis;
import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.exception.ProductNotFoundException;
import com.berkaykomur.backend.mapper.AnalysisMapper;
import com.berkaykomur.backend.model.Analysis;
import com.berkaykomur.backend.model.Product;
import com.berkaykomur.backend.model.Status;
import com.berkaykomur.backend.repository.AnalysisRepository;
import com.berkaykomur.backend.repository.ProductRepository;
import com.berkaykomur.backend.scrapper.Scrapper;
import com.berkaykomur.backend.service.AiAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiAnalysisServiceImpl implements AiAnalysisService {

    private final AiAnalysis aiAnalysis;
    private final AnalysisMapper analysisMapper;
    private final AnalysisRepository analysisRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public AnalysisResult createAnalysis (Scrapper scrapper,Long productId) {
        log.info("Analiz süreci başlatıldı. Product ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Id'ye göre ürün bulunamadı: " + productId));

        Optional<Analysis> analysis = analysisRepository.getAnalysisByProduct_Id(productId);
        if(analysis.isPresent()){
            log.info("Veritabanında mevcut analiz bulundu. Yeniden AI isteği atılmayacak. Product ID: {}", productId);
            return analysisMapper.toAnalysisResult(analysis.get());
        }
        String productUrl=product.getProductUrl();
        log.debug("Yapay zeka analizi için istek atılıyor. URL: {}", productUrl);
        AnalysisResult analysisResult=aiAnalysis.analyzeComments(scrapper,productUrl);
        if(analysisResult==null ){
            log.warn("Yapay zeka analizi başarısız oldu (Sonuç null döndü). Ürün FAILED durumuna çekiliyor. Product ID: {}", productId);
            Analysis failedAnalysis=Analysis.builder()
                    .product(product)
                    .status(Status.FAILED)
                    .build();
            analysisRepository.save(failedAnalysis);
            return analysisMapper.toAnalysisResult(failedAnalysis);
        }

        Analysis toAnalysis=analysisMapper.toAnalysis(analysisResult);
        toAnalysis.setProduct(product);
        toAnalysis.setStatus(Status.SUCCESS);
        log.info("Yapay zeka analizi başarıyla tamamlandı. Veritabanına kaydediliyor. Product ID: {}", productId);
        analysisRepository.save(toAnalysis);

        return analysisMapper.toAnalysisResult(toAnalysis);

    }


}
