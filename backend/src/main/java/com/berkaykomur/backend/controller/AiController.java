package com.berkaykomur.backend.controller;

import com.berkaykomur.backend.dto.ProductAnalysisCombinedResponse;
import com.berkaykomur.backend.dto.ProductAnalyzeRequest;
import com.berkaykomur.backend.exception.ProductNotFoundException;
import com.berkaykomur.backend.model.Analysis;
import com.berkaykomur.backend.model.Status;
import com.berkaykomur.backend.repository.AnalysisRepository;
import com.berkaykomur.backend.service.impl.ScrapAndAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController {
    private final ScrapAndAnalysisService  scrapAndAnalysisService;
    private final AnalysisRepository analysisRepository;

//    @PostMapping("/analyze")
//    public CompletableFuture<ResponseEntity<ProductAnalysisCombinedResponse>> analyze(@RequestBody ProductAnalyzeRequest request) {
//        return scrapAndAnalysisService.scrapAndAnalysis(request.productUrl(),false)
//                .thenApply(ResponseEntity::ok)
//                .exceptionally(ex -> {
//                    log.error("Analiz sırasında kritik hata: {}", ex.getMessage());
//                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//                });
//    }
        @PostMapping("/analyze")
        public ResponseEntity<Map<String, Object>> analyze(@RequestBody ProductAnalyzeRequest request) {

            // 1. İşlemi DB'ye kaydet ve ID'yi al (Senkron çalışır, hemen biter)
            Long productId = scrapAndAnalysisService.initiateAnalysis(request.productUrl(), false);

            // 2. Asenkron işlemi Controller üzerinden tetikle (Proxy'den geçtiği için asenkron BAŞLAR)
            scrapAndAnalysisService.startAsyncProcess(productId, request.productUrl(), false);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                    "message", "İşlem sıraya alındı, analiz ediliyor.",
                    "productId", productId
            ));
        }
    @GetMapping("/status/{productId}")
    public ResponseEntity<?> checkStatus(@PathVariable Long productId) {
        // DB'ye git statüye bak
        Analysis analysis = analysisRepository.getAnalysisByProduct_Id(productId)
                .orElseThrow(() -> new ProductNotFoundException("Bulunamadı"));

        if (analysis.getStatus() == Status.PENDING) {
            return ResponseEntity.ok(Map.of("status", "PENDING"));
        }

        if (analysis.getStatus() == Status.FAILED) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAILED", "message", "Analiz başarısız."));
        }

        // Eğer SUCCESSFUL ise senin mevcut metodunla gerçek veriyi dön
        return ResponseEntity.ok(scrapAndAnalysisService.getAnalysisById(productId));
    }

//    @PostMapping("/re-analyze")
//    public CompletableFuture<ResponseEntity<ProductAnalysisCombinedResponse>> reAnalyzeComments(@RequestBody ProductAnalyzeRequest request) {
//        return scrapAndAnalysisService.scrapAndAnalysis(request.productUrl(),true)
//                .thenApply(ResponseEntity::ok)
//                .exceptionally(ex -> {
//                    log.error("Yeniden analiz sırasında kritik hata: {}", ex.getMessage());
//                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//                });
//    }
    @PostMapping("/re-analyze")
    public ResponseEntity<Map<String, Object>> reAnalyzeComments(@RequestBody ProductAnalyzeRequest request) {

        // 1. ForceRefresh true olarak başlat
        Long productId = scrapAndAnalysisService.initiateAnalysis(request.productUrl(), true);

        // 2. Asenkron işlemi Controller üzerinden tetikle (forceRefresh = true)
        scrapAndAnalysisService.startAsyncProcess(productId, request.productUrl(), true);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "message", "Yeniden analiz işlemi sıraya alındı.",
                "productId", productId
        ));
    }
    @GetMapping("/latest")
    public ResponseEntity<ProductAnalysisCombinedResponse> getLatest() {
        return ResponseEntity.ok(scrapAndAnalysisService.getLatestAnalysis());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductAnalysisCombinedResponse> getAnalysisById(@PathVariable("productId") Long productId) {
        return ResponseEntity.ok(scrapAndAnalysisService.getAnalysisById(productId));
    }

}


