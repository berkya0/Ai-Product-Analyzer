package com.berkaykomur.backend.controller;

import com.berkaykomur.backend.ai.AiAnalysis;
import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.dto.ProductResponse;
import com.berkaykomur.backend.scrapper.Scrapper;
import com.berkaykomur.backend.scrapper.impl.TrendyolScrapper;
import com.berkaykomur.backend.service.ScrapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final ScrapperService scrapperService;
    private final AiAnalysis aiAnalysis;
    private final TrendyolScrapper trendyolScrapper;
    @GetMapping("/scrap")
    public ResponseEntity<ProductResponse> analyzeComments(@RequestParam String url) {
        return ResponseEntity.ok(scrapperService.executeScrapping(url));
    }
    @GetMapping("/analysis")
    public ResponseEntity<AnalysisResult> analyzeComment(@RequestParam String url) {
        return ResponseEntity.ok(aiAnalysis.analyzeComments(trendyolScrapper,url));
    }

}
