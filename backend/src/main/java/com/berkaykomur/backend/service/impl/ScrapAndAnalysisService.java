package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.dto.ProductAnalysisCombinedResponse;
import com.berkaykomur.backend.dto.ProductResponse;
import com.berkaykomur.backend.exception.ProductNotFoundException;
import com.berkaykomur.backend.mapper.AnalysisMapper;
import com.berkaykomur.backend.mapper.ProductMapper;
import com.berkaykomur.backend.model.Analysis;
import com.berkaykomur.backend.model.Product;
import com.berkaykomur.backend.model.Status;
import com.berkaykomur.backend.repository.AnalysisRepository;
import com.berkaykomur.backend.repository.ProductRepository;
import com.berkaykomur.backend.scrapper.Scrapper;
import com.berkaykomur.backend.service.AiAnalysisService;
import com.berkaykomur.backend.service.ScrapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScrapAndAnalysisService {

    private final ScrapperService scrapperService;
    private final AiAnalysisService aiAnalysisService;
    private final AnalysisRepository analysisRepository;
    private final AnalysisMapper analysisMapper;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;


//    @Async("analysisTaskExecutor")
//    public CompletableFuture<ProductAnalysisCombinedResponse> scrapAndAnalysis(String productUrl, boolean forceRefresh) {
//        log.info("Ürün kazıma ve analiz süreci başlatıldı. URL: {}, ForceRefresh: {}", productUrl, forceRefresh);
//
//        ProductResponse scrappedProduct = scrapperService.executeScrapping(productUrl, forceRefresh);
//        Scrapper scrapper = scrapperService.getScrapper(productUrl);
//        AnalysisResult analysisResult = aiAnalysisService.createAnalysis(scrapper, scrappedProduct.id(), forceRefresh);
//
//        log.info("Ürün kazıma ve analiz süreci başarıyla tamamlandı. Product ID: {}", scrappedProduct.id());
//        ProductAnalysisCombinedResponse response=new ProductAnalysisCombinedResponse(scrappedProduct, analysisResult);
//        return CompletableFuture.completedFuture(response);
//
//    }
    @Async("analysisTaskExecutor")
    public void startAsyncProcess(Long productId, String productUrl, boolean forceRefresh) {
        log.info("Ürün analizi işlenmeye başlandı. Product ID: {} forceRefresh:{}", productId,forceRefresh);
        try {
            Scrapper scrapper = scrapperService.getScrapper(productUrl);
            aiAnalysisService.createAnalysis(scrapper, productId, forceRefresh);

        } catch (Exception e) {
            log.error("İşlem sırasında hata oldu, FAILED yapılıyor. ID: {}", productId, e);
            analysisRepository.getAnalysisByProduct_Id(productId).ifPresent(analysis -> {
                analysis.setStatus(Status.FAILED);
                analysisRepository.save(analysis);
            });
        }
    }
    @Transactional
    public Long initiateAnalysis(String productUrl, boolean forceRefresh) {
        ProductResponse scrappedProduct = scrapperService.executeScrapping(productUrl, forceRefresh);
        Product product = productRepository.findProductIncludingDeleted(scrappedProduct.productUrl()).
                orElseThrow(()-> new ProductNotFoundException("Ürün bilgileri çekilemedi :"+scrappedProduct.productUrl()));

        Optional<Analysis> analysis = analysisRepository.getAnalysisByProduct_Id(product.getId());
        if(analysis.isEmpty()){
            log.info("Yeni analiz oluşturuluyorr status:PENDING");
            Analysis newAnalysis = new Analysis();
            newAnalysis.setStatus(Status.PENDING);
            newAnalysis.setProduct(product);
            analysisRepository.save(newAnalysis);
        }
        return product.getId();
    }

    public ProductAnalysisCombinedResponse getLatestAnalysis() {
        log.info("En son yapılan analiz sorgulanıyor...");
        Optional<Analysis> latestAnalysis = analysisRepository.findFirstByOrderByCreatedAtDesc();

        if (latestAnalysis.isEmpty()) {
            log.warn("Sistemde kayıtlı herhangi bir analiz bulunamadı.");
            return new ProductAnalysisCombinedResponse(null, null);
        }

        Product latesAnalsisProduct = latestAnalysis.get().getProduct();
        AnalysisResult latestAnalysisDto = analysisMapper.toAnalysisResult(latestAnalysis.get());
        ProductResponse latestProductDto = productMapper.toProductResponse(latesAnalsisProduct);

        log.info("En son analiz başarıyla getirildi. Product ID: {}", latesAnalsisProduct.getId());
        return new ProductAnalysisCombinedResponse(latestProductDto, latestAnalysisDto);
    }

    public ProductAnalysisCombinedResponse getAnalysisById(Long productId) {
        log.info("Product ID ile analiz sorgulanıyor. ID: {}", productId);

        Analysis analysis = analysisRepository.getAnalysisByProduct_Id(productId).
                orElseThrow(() -> {
                    log.error("Ürün ID ile bulunamadı. ID: {}", productId);
                    return new ProductNotFoundException("Ürün id ile bulunamadı: " + productId);
                });

        AnalysisResult analysisDto = analysisMapper.toAnalysisResult(analysis);
        ProductResponse productDto = productMapper.toProductResponse(analysis.getProduct());

        log.info("Analiz ID üzerinden başarıyla getirildi. Product ID: {}", productId);
        return new ProductAnalysisCombinedResponse(productDto, analysisDto);
    }
}