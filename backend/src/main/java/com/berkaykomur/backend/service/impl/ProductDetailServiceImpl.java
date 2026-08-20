package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.exception.ProductNotFoundException;
import com.berkaykomur.backend.model.Product;
import com.berkaykomur.backend.repository.ProductRepository;
import com.berkaykomur.backend.service.ProductDetailService;
import com.berkaykomur.backend.service.ScrapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductDetailServiceImpl implements ProductDetailService {

    private final ProductRepository productRepository;
    private final ScrapperService scrapperService;
    @Override
    public void deleteProductDetailById(Long id) {
        Product product=productRepository.findById(id)
                .orElseThrow(()->new ProductNotFoundException("Ürün idye göre bulunamadı: "+id));
        productRepository.delete(product);
    }

    public void reProduct(String productUrl){
        Product scrappedProduct=scrapperService.getScrappedProduct(productUrl);
        productRepository.save(scrappedProduct);

    }

}
