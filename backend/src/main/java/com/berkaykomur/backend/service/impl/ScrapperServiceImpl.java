package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.dto.ProductResponse;
import com.berkaykomur.backend.dto.ScrapperResult;
import com.berkaykomur.backend.exception.analysis.UnspportedMarketPlaceException;
import com.berkaykomur.backend.exception.user.UsernameNotFoundException;
import com.berkaykomur.backend.mapper.ProductMapper;
import com.berkaykomur.backend.model.Product;
import com.berkaykomur.backend.model.Status;
import com.berkaykomur.backend.model.UserEntity;
import com.berkaykomur.backend.repository.ProductRepository;
import com.berkaykomur.backend.repository.UserRepository;
import com.berkaykomur.backend.scrapper.Scrapper;
import com.berkaykomur.backend.service.ScrapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScrapperServiceImpl implements ScrapperService {
    private final List<Scrapper> scrappers;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private final UserRepository userRepository;

    @Transactional
    @Override
    public ProductResponse executeScrapping(String productUrl, boolean forceRefresh,Long userId) {

        log.info("Kullanıcı: {} -> Ürün kazıma işlemi başlatıldı. URL: {}", userId, productUrl);
        log.info("Ürün kazıma (scrapping) işlemi başlatıldı. URL: {}, ForceRefresh: {}", productUrl, forceRefresh);

        Optional<Product> product = productRepository.findProductIncludingDeletedAndUser_Id(productUrl,userId);
        if (product.isPresent() && !forceRefresh && product.get().getAnalyses().getStatus()== Status.SUCCESS) {
            log.info("Ürün veritabanında bulundu ve forceRefresh=false. Ürün aktifleştiriliyor/döndürülüyor. Product ID: {}", product.get().getId());
            Product existingProduct = product.get();
            productRepository.restoreProduct(existingProduct.getId());
            return productMapper.toProductResponse(existingProduct);
        }

        Scrapper scrapper = getScrapper(productUrl);
        log.info("Uygun scrapper bulundu: {}. Scrap işlemi gerçekleştiriliyor...", scrapper.getClass().getSimpleName());

        ScrapperResult scrapperResponse = scrapper.scrap(productUrl);
        Product savedProduct;

        //analizli ürün tekrar analiz edilmek istenirse
        if (product.isPresent()&& product.get().getAnalyses().getStatus()== Status.SUCCESS) {
            log.info("Seçilen ürün bilgileri güncelleniyor. Product ID: {}", product.get().getId());
            Product existingProduct = product.get();
            productMapper.updateProductFromDto(scrapperResponse, existingProduct);
            savedProduct = productRepository.save(existingProduct);
        } else {
            log.info("Yeni ürün veritabanına kaydediliyor.");

            Product newProduct = productMapper.toProduct(scrapperResponse);

            UserEntity user=userRepository.findById(userId)
                    .orElseThrow(()->new UsernameNotFoundException("Kullanıcı bulunamadı id: "+userId));
            newProduct.setUser(user);
            savedProduct = productRepository.save(newProduct);
        }

        log.info("Ürün kazıma işlemi başarıyla tamamlandı. Product ID: {}", savedProduct.getId());
        return productMapper.toProductResponse(savedProduct);
    }

    @Override
    public Scrapper getScrapper(String productUrl) {
        log.debug("URL için uygun scrapper aranıyor. URL: {}", productUrl);

        return scrappers.stream()
                .filter(s -> s.supports(productUrl))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Desteklenmeyen pazar yeri/URL hatası alındı. URL: {}", productUrl);
                    return new UnspportedMarketPlaceException("URL'yi destekleyen site bulunamadı: " + productUrl);
                });
    }
}