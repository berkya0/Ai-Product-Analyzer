package com.berkaykomur.backend.controller;

import com.berkaykomur.backend.dto.CompareRequest;
import com.berkaykomur.backend.dto.CompareResults;
import com.berkaykomur.backend.dto.ProductResponse;
import com.berkaykomur.backend.service.ProductService;
import com.berkaykomur.backend.service.ScrapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ScrapperService scrapperService;
    private final ProductService productService;
//    @PostMapping("scrap")
//    public ResponseEntity<ProductResponse> scrap(@RequestParam String productUrl) {
//        return ResponseEntity.ok(scrapperService.executeScrapping(productUrl));
//    } //çalışmıyor şuanlık silinebilir

    @GetMapping("get/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
       // return ResponseEntity.ok(productDetailService.getProductDetailById(id));
        return null; //ihtiyac kalktı belki komple kaldırılır
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProductDetailById(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/set-following/{productId}/follow")
    public ResponseEntity<Void> setFollowing(@PathVariable Long productId,@RequestParam boolean isFollowing){
        productService.setFollow(productId,isFollowing);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/compare")
    public ResponseEntity<List<CompareResults>> compareProducts(@RequestBody CompareRequest request) {
        return ResponseEntity.ok(productService.compareProducts(request.productIds()));

    }
}
