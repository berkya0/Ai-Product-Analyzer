package com.berkaykomur.backend.controller;

import com.berkaykomur.backend.dto.ProductResponse;
import com.berkaykomur.backend.service.ScrapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ScrapperService scrapperService;
    @PostMapping("scrap")
    public ResponseEntity<ProductResponse> scrap(@RequestParam String productUrl) {
        return ResponseEntity.ok(scrapperService.executeScrapping(productUrl));
    }
}
