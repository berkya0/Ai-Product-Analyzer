package com.berkaykomur.backend.controller;

import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.service.AiAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiAnalysisService aiAnalysisService;

    @GetMapping("/analyze/{productId}")
    public ResponseEntity<AnalysisResult> analyzeComments(@PathVariable Long productId) {
        return ResponseEntity.ok(aiAnalysisService.createAnalysis(productId));
    }
}


