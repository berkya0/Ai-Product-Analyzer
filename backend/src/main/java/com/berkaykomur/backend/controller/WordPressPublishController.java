package com.berkaykomur.backend.controller;

import com.berkaykomur.backend.dto.ProductAnalysisCombinedResponse;
import com.berkaykomur.backend.service.WordPressHtmlBuilderService;
import com.berkaykomur.backend.service.WordPressPublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wordpress")
@RequiredArgsConstructor
public class WordPressPublishController {

    private final WordPressPublisherService publisherService;
    private final WordPressHtmlBuilderService htmlBuilderService;

    @PostMapping("/publish/{siteId}")
    public ResponseEntity<String> publishToWordPress(
            @PathVariable Long siteId,
            @RequestBody ProductAnalysisCombinedResponse combinedData) {

        String wpResponse = publisherService.publish(siteId, combinedData);
        return ResponseEntity.ok(wpResponse);
    }
    @PostMapping("/preview")
    public ResponseEntity<String> previewHtml(@RequestBody ProductAnalysisCombinedResponse combinedData) {
        String generatedHtml = htmlBuilderService.buildHtml(combinedData.product(), combinedData.analysis());
        return ResponseEntity.ok(generatedHtml);
    }
}