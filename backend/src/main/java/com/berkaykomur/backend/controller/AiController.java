package com.berkaykomur.backend.controller;

import com.berkaykomur.backend.dto.ProductAnalysisCombinedResponse;
import com.berkaykomur.backend.dto.ProductAnalyzeRequest;
import com.berkaykomur.backend.service.AiAnalysisService;
import com.berkaykomur.backend.service.impl.ScrapAndAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {
    private final ScrapAndAnalysisService  scrapAndAnalysisService;

    @PostMapping("/analyze")
    public ResponseEntity<ProductAnalysisCombinedResponse> analyzeComments(@RequestBody ProductAnalyzeRequest request) {
        return ResponseEntity.ok(scrapAndAnalysisService.scrapAndAnalysis(request.productUrl(),false));


    }
    @PostMapping("/re-analyze")
    public ResponseEntity<ProductAnalysisCombinedResponse> reAnalyzeComments(@RequestBody ProductAnalyzeRequest request) {
        return ResponseEntity.ok(scrapAndAnalysisService.scrapAndAnalysis(request.productUrl(), true));
    }
}


