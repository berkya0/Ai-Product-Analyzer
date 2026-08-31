package com.berkaykomur.backend.controller;

import com.berkaykomur.backend.dto.CompareRequest;
import com.berkaykomur.backend.dto.CompareResults;
import com.berkaykomur.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

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
