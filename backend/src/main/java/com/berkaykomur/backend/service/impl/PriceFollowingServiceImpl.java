package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.exception.ProductNotFoundException;
import com.berkaykomur.backend.model.Product;
import com.berkaykomur.backend.repository.ProductRepository;
import com.berkaykomur.backend.service.PriceFollowingService;
import com.berkaykomur.backend.service.ScrapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceFollowingServiceImpl implements PriceFollowingService {

    private final ProductRepository productRepository;
    private final ScrapperService scrapperService;

    @Transactional
    @Override
    public void setFollow(Long productId,boolean isFollowing){
        log.info("Ürünün takip durumu değiştirlecek. productId:{} isFollowing:{}",productId,isFollowing);
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ProductNotFoundException("Takip ayarı için seçilen ürün bulunamadı "));
        product.setFollowing(isFollowing);
        log.info("Takip durumu değiştirildi. Durum:{}",isFollowing);
        productRepository.save(product);
    }

    @Scheduled(cron = "0 0 3 * * ?")
    @Async
    @Override
    public void updateFollowedProductPrices() {
        log.info("Otomatik fiyat takip ve güncelleme işlemi başlatıldı.");

        List<Product> followedProducts = productRepository.findAllByIsFollowingIsTrue();
        if (followedProducts.isEmpty()) {
            log.info("Takip edilen hiçbir ürün bulunamadı.");
            return;
        }

        for (Product product : followedProducts) {
            try {
                log.info("Takip edilen ürün güncelleniyor. ID: {}, URL: {}", product.getId(), product.getProductUrl());
                scrapperService.executeScrapping(product.getProductUrl(), true);

                Thread.sleep(4000);

            } catch (Exception e) {
                log.error("Ürün güncellenirken hata oluştu! Product ID: {}", product.getId(), e);
            }
        }

        log.info("Otomatik fiyat güncelleme işlemi tamamlandı.");
    }


}
