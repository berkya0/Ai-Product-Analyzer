package com.berkaykomur.backend.service;

public interface PriceFollowingService {
    void setFollow(Long productId,boolean isFollowing);
    void updateFollowedProductPrices();
}
