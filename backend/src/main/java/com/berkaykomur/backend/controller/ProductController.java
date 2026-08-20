package com.berkaykomur.backend.controller;

import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.dto.ProductResponse;
import com.berkaykomur.backend.service.ProductDetailService;
import com.berkaykomur.backend.service.ScrapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ScrapperService scrapperService;
    private final ProductDetailService productDetailService;
    @PostMapping("scrap")
    public ResponseEntity<ProductResponse> scrap(@RequestParam String productUrl) {
        return ResponseEntity.ok(scrapperService.executeScrapping(productUrl));
    }

    @GetMapping("get/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
       // return ResponseEntity.ok(productDetailService.getProductDetailById(id));
        return null; //ihtiyac kalktı belki komple kaldırılır
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productDetailService.deleteProductDetailById(id);
        return ResponseEntity.noContent().build();
    }
}
