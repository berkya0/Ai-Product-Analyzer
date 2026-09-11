package com.berkaykomur.backend.controller;

import com.berkaykomur.backend.dto.CompareRequest;
import com.berkaykomur.backend.dto.CompareResults;
import com.berkaykomur.backend.jwt.CustomUserDetails;
import com.berkaykomur.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId,@AuthenticationPrincipal CustomUserDetails user) {
        productService.deleteProductDetailById(productId, user.getUserId());
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/set-following/{productId}")
    public ResponseEntity<Void> setFollowing(@PathVariable Long productId,@RequestParam boolean isFollowing,
                                             @AuthenticationPrincipal CustomUserDetails user){
        productService.setFollow(productId,isFollowing,user.getUserId());
        return ResponseEntity.ok().build();
    }
    @PostMapping("/compare")
    public ResponseEntity<List<CompareResults>> compareProducts(@RequestBody CompareRequest request,
                                                                @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(productService.compareProducts(request.productIds(), user.getUserId()));

    }
}
