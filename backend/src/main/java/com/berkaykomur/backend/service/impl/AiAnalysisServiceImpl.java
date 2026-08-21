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
    public AnalysisResult createAnalysis (Scrapper scrapper,Long productId,boolean forceRefresh) {
        log.info("Analiz süreci başlatıldı. Product ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Id'ye göre ürün bulunamadı: " + productId));

        Optional<Analysis> existingAnalysisOp = analysisRepository.getAnalysisByProduct_Id(productId);
        if(existingAnalysisOp.isPresent()&&!forceRefresh){
            log.info("Veritabanında mevcut analiz bulundu. Yeniden AI isteği atılmayacak. Product ID: {}", productId);
            return analysisMapper.toAnalysisResult(existingAnalysisOp.get());
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

        Analysis analysisEntity;
        if (existingAnalysisOp.isPresent()) {
            log.info("Daha önceden analiz edilen ürün tekrar analiz ediliyor. Product ID: {}", productId);
            analysisEntity = existingAnalysisOp.get();
            analysisMapper.updateAnalysisFromDto(analysisResult, analysisEntity);
        } else {
            analysisEntity = analysisMapper.toAnalysis(analysisResult);
        }

        analysisEntity.setProduct(product);
        analysisEntity.setStatus(Status.SUCCESS);
        log.info("Yapay zeka analizi başarılı oldu");
        analysisRepository.save(analysisEntity);
        return analysisMapper.toAnalysisResult(analysisEntity);

    }


}
