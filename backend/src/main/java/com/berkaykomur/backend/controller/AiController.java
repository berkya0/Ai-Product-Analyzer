package com.berkaykomur.backend.controller;

import com.berkaykomur.backend.dto.ProductAnalysisCombinedResponse;
import com.berkaykomur.backend.dto.ProductAnalyzeRequest;
import com.berkaykomur.backend.exception.product.ProductNotFoundException;
import com.berkaykomur.backend.jwt.CustomUserDetails;
import com.berkaykomur.backend.model.Analysis;
import com.berkaykomur.backend.model.Status;
import com.berkaykomur.backend.repository.AnalysisRepository;
import com.berkaykomur.backend.service.impl.ScrapAndAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController {
    private final ScrapAndAnalysisService  scrapAndAnalysisService;
    private final AnalysisRepository analysisRepository;

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyze(@RequestBody ProductAnalyzeRequest request,
                                                       @AuthenticationPrincipal CustomUserDetails currentUser) {

        Long productId = scrapAndAnalysisService.initiateAnalysis(request.productUrl(), false, currentUser.getUserId());
        scrapAndAnalysisService.startAsyncProcess(productId, request.productUrl(), false, currentUser.getUserId());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "message", "İşlem sıraya alındı, analiz ediliyor.",
                "productId", productId
        ));
    }
    @GetMapping("/status/{productId}")
    public ResponseEntity<?> checkStatus(@PathVariable Long productId,
                                         @AuthenticationPrincipal CustomUserDetails currentUser) {

        Analysis analysis = analysisRepository.getAnalysisByProduct_IdAndProduct_User_Id(productId,currentUser.getUserId())
                .orElseThrow(() -> new ProductNotFoundException("Bulunamadı"));

        if (analysis.getStatus() == Status.PENDING) {
            return ResponseEntity.ok(Map.of("status", "PENDING"));
        }

        if (analysis.getStatus() == Status.FAILED) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAILED", "message", "Analiz başarısız."));
        }
        return ResponseEntity.ok(scrapAndAnalysisService.getAnalysisById(productId, currentUser.getUserId()));
    }

    @PostMapping("/re-analyze")
    public ResponseEntity<Map<String, Object>> reAnalyzeComments(@RequestBody ProductAnalyzeRequest request,
                                                                 @AuthenticationPrincipal CustomUserDetails currentUser) {

        Long productId = scrapAndAnalysisService.initiateAnalysis(request.productUrl(), true, currentUser.getUserId());
        scrapAndAnalysisService.startAsyncProcess(productId, request.productUrl(), true,currentUser.getUserId());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "message", "Yeniden analiz işlemi sıraya alındı.",
                "productId", productId
        ));
    }
    @GetMapping("/latest")
    public ResponseEntity<ProductAnalysisCombinedResponse> getLatest(@AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(scrapAndAnalysisService.getLatestAnalysis(currentUser.getUserId()));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductAnalysisCombinedResponse> getAnalysisById(@PathVariable("productId") Long productId,
                                                                           @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.ok(scrapAndAnalysisService.getAnalysisById(productId,currentUser.getUserId()));
    }

}


