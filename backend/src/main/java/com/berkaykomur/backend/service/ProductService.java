package com.berkaykomur.backend.service;

import com.berkaykomur.backend.dto.CompareResults;

import java.util.List;

public interface ProductService {
    void setFollow(Long productId,boolean isFollowing,Long userId);
    void updateFollowedProductPrices();
    void deleteProductDetailById(Long id,Long userId);
    List<CompareResults> compareProducts(List<Long> productIds,Long  userId);
}
