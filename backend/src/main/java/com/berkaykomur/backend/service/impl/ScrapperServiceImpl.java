package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.ai.AiAnalysis;
import com.berkaykomur.backend.dto.ProductResponse;
import com.berkaykomur.backend.dto.ScrapperResponse;
import com.berkaykomur.backend.exception.ProductNotFoundException;
import com.berkaykomur.backend.exception.UnspportedMarketPlaceException;
import com.berkaykomur.backend.mapper.ProductMapper;
import com.berkaykomur.backend.model.Product;
import com.berkaykomur.backend.repository.ProductRepository;
import com.berkaykomur.backend.scrapper.Scrapper;
import com.berkaykomur.backend.service.ScrapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScrapperServiceImpl implements ScrapperService {
     private final List<Scrapper> scrappers;
     private final ProductRepository productRepository;
     private final ProductMapper productMapper;
     private final AiAnalysis aiAnalysis;

     @Transactional
     ProductResponse executeScrapping(String url){
         Scrapper scrapper=scrappers.stream()
                 .filter(s->s.supports(url))
                 .findFirst()
                 .orElseThrow(()-> new UnspportedMarketPlaceException("URL'yi destekleyen site bulunamadı: "+url));
         ScrapperResponse productResponse= scrapper.scrap(url);
         Product product=productRepository.findByProductUrl(url)
                 .map(existingProduct-> {
                     productMapper.updateProductFromDto(productResponse, existingProduct);
                     return existingProduct;
                 })
                 .orElseGet(()-> {
                     return productMapper.toProduct(productResponse);
                 });
         Product savedProduct= productRepository.save(product);
         aiAnalysis.analyzeComments(scrapper,url);
         return productMapper.toProductResponse(savedProduct);

     }
}
