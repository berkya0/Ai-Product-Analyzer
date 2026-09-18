package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.dto.CompareResults;
import com.berkaykomur.backend.exception.product.CategoryMismatchException;
import com.berkaykomur.backend.exception.product.ProductNotFoundException;
import com.berkaykomur.backend.mapper.AnalysisMapper;
import com.berkaykomur.backend.model.Analysis;
import com.berkaykomur.backend.model.Product;
import com.berkaykomur.backend.repository.AnalysisRepository;
import com.berkaykomur.backend.repository.ProductRepository;
import com.berkaykomur.backend.service.ProductService;
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
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ScrapperService scrapperService;
    private final AnalysisRepository analysisRepository;
    private final AnalysisMapper analysisMapper;

    @Transactional
    @Override
    public void setFollow(Long productId,boolean isFollowing,Long userId){

        log.info("Kullanıcı id: {} -> Takip durumu değiştirilecek. ID: {}", userId, productId);

        Product product = productRepository.findByIdAndUser_Id(productId, userId)
                .orElseThrow(() -> new ProductNotFoundException("Ürün bulunamadı veya bu işlem için yetkiniz yok!"));

        product.setFollowing(isFollowing);
        log.info("Takip durumu değiştirildi. Durum:{}",isFollowing);
        productRepository.save(product);
    }

    @Scheduled(cron = "0 0 3 * * ?")
    @Async
    @Override
    public void updateFollowedProductPrices() {  // buna sonra bak
        log.info("Otomatik takip ve güncelleme işlemi başlatıldı");
        List<Product> followedProducts = productRepository.findAllByIsFollowingIsTrue();
        if (followedProducts.isEmpty()) {
            log.info("Takip edilen hiçbir ürün bulunamadı.");
            return;
        }

        for (Product product : followedProducts) {
            try {
                log.info("Takip edilen ürün güncelleniyor. ID: {}, URL: {}", product.getId(), product.getProductUrl());
                scrapperService.executeScrapping(product.getProductUrl(), true,product.getUser().getId());
                Thread.sleep(4000);

            } catch (Exception e) {
                log.error("Ürün güncellenirken hata oluştu! Product ID: {}", product.getId(), e);
            }
        }

        log.info("Otomatik güncelleme işlemi tamamlandı.");
    }

    @Override
    public void deleteProductDetailById(Long id,Long userId) {
        log.info("{} id'li kullanıcının ürünü silinecek,productID: {}", userId, id);
        Product product=productRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(()->new ProductNotFoundException("Ürün idye göre bulunamadı: "+id));
        productRepository.delete(product);
    }

    @Override
    public List<CompareResults> compareProducts(List<Long> productIds,Long userId){
        log.info("Seçilen ürünler karşılaştırılacak. Product IDs: {}", productIds);
        if (productIds == null || productIds.size() != 2) {
            throw new IllegalArgumentException("Karşılaştırma için tam olarak 2 ürün seçilmelidir.");
        }

        List<Analysis> analyses = analysisRepository.findAllByProduct_IdInAndProduct_User_Id(productIds,userId);
        if (analyses.size() != 2) {
            throw new ProductNotFoundException("Karşılaştırılmak istenen ürünlerden biri veya birkaçı bulunamadı ya da erişim yetkiniz yok.");
        }
        if (!analyses.getFirst().getProduct().getCategory().equals(analyses.getLast().getProduct().getCategory())) {
            String message = String.format("Farklı kategorideki ürünler karşılaştırılamaz! Ürün 1: %s, Ürün 2: %s",
                    analyses.getFirst().getProduct().getCategory(),
                    analyses.getLast().getProduct().getCategory());
            throw new CategoryMismatchException(message);
        }
        return analyses.stream()
                .map(analysisMapper::toCompareResults)
                .toList();
    }

}
