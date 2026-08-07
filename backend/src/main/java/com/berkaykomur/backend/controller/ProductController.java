package com.berkaykomur.backend.controller;

import com.berkaykomur.backend.scrapper.impl.TrendyolScrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final TrendyolScrapper trendyolScrapper;

    @PutMapping("save")
    public ResponseEntity<Void> trendyolScrap(@RequestParam String productUrl) throws IOException {
        trendyolScrapper.trendyolScrapper(productUrl);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
