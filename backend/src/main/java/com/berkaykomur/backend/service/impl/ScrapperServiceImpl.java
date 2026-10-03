package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.dto.ProductResponse;
import com.berkaykomur.backend.dto.ScrapperResult;

import com.berkaykomur.backend.exception.analysis.UnspportedMarketPlaceException;
import com.berkaykomur.backend.exception.user.UserNotFoundException;
import com.berkaykomur.backend.mapper.ProductMapper;
import com.berkaykomur.backend.model.Product;
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
@Transactional(readOnly = true)
public class ScrapperServiceImpl implements ScrapperService {

    private final List<Scrapper> scrappers;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ProductResponse executeScrapping(String productUrl, boolean forceRefresh, Long userId) {
        log.info("Kullanıcı: {} -> Ürün kazıma işlemi başlatıldı. URL: {}", userId, productUrl);

        Optional<Product> optionalProduct = productRepository.findProductIncludingDeletedAndUser_Id(productUrl, userId);

        if (optionalProduct.isEmpty()) {
            Product newProduct = createAndSaveProduct(productUrl, userId);
            log.info("Yeni ürün başarıyla kaydedildi. Product ID: {}", newProduct.getId());
            return productMapper.toProductResponse(newProduct);
        }

        Product existingProduct = optionalProduct.get();
        productRepository.restoreProduct(existingProduct.getId());

        if (forceRefresh) {
            log.info("Seçilen ürün bilgileri güncelleniyor. Product ID: {}", existingProduct.getId());
            ScrapperResult scrapperResponse = scrapUrl(productUrl);
            productMapper.updateProductFromDto(scrapperResponse, existingProduct);
        } else {
            log.info("Ürün veritabanında bulundu (forceRefresh=false). Product ID: {}", existingProduct.getId());
        }

        return productMapper.toProductResponse(existingProduct);
    }

    private Product createAndSaveProduct(String productUrl, Long userId) {
        log.info("Yeni ürün veritabanına kaydediliyor.");
        ScrapperResult scrapperResponse = scrapUrl(productUrl);
        Product newProduct = productMapper.toProduct(scrapperResponse);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı id: " + userId));

        newProduct.setUser(user);
        return productRepository.save(newProduct);
    }

    private ScrapperResult scrapUrl(String productUrl) {
        Scrapper scrapper = getScrapper(productUrl);
        log.info("Uygun scrapper bulundu: {}. Scrap işlemi gerçekleştiriliyor...", scrapper.getClass().getSimpleName());
        return scrapper.scrap(productUrl);
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