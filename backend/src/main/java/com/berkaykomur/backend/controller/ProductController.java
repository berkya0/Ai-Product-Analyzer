package com.berkaykomur.backend.controller;

import com.berkaykomur.backend.dto.ProductResponse;
import com.berkaykomur.backend.service.ScrapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ScrapperService scrapperService;
    @PutMapping("scrap")
    public ResponseEntity<ProductResponse> scrap(@RequestParam String productUrl) {
        return ResponseEntity.ok(scrapperService.executeScrapping(productUrl));
    }
}
