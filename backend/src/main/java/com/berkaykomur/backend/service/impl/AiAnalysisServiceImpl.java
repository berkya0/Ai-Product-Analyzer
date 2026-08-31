package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.ai.AiAnalysis;
import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.exception.AiAnalaysisNotFoundException;
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
        if(existingAnalysisOp.isPresent() && !forceRefresh) {
            Analysis existingAnalysis = existingAnalysisOp.get();
            if (existingAnalysis.getStatus() == Status.SUCCESS) {
                log.info("Veritabanında mevcut analiz bulundu. Yeniden AI isteği atılmayacak. Product ID: {}", productId);
                return analysisMapper.toAnalysisResult(existingAnalysis);
            }
            log.info("Mevcut analiz PENDING durumunda, analiz baştan işlenecek. Product ID: {}", productId);
        }

        String productUrl=product.getProductUrl();
        log.debug("Yapay zeka analizi için istek atılıyor. URL: {}", productUrl);
        AnalysisResult analysisResult=aiAnalysis.analyzeComments(scrapper,productUrl);
        if(analysisResult==null ){
            throw new AiAnalaysisNotFoundException("Analiz sonuçları null döndü. Analiz yapılamadı: "+productUrl);

        }

        if (existingAnalysisOp.isEmpty()) {
           throw new AiAnalaysisNotFoundException("Analiz akışında hata PENDING analiz yok");
        }
        Analysis analysisEntity=existingAnalysisOp.get();
        log.info("Ürün analizi oluşşturuluyor. Product ID: {}", productId);
        analysisMapper.updateAnalysisFromDto(analysisResult, analysisEntity);
        analysisEntity.setStatus(Status.SUCCESS);
        analysisEntity.setProduct(product);
        log.info("Yapay zeka analizi başarılı oldu");
        analysisRepository.save(analysisEntity);

        return analysisMapper.toAnalysisResult(analysisEntity);

    }


}
