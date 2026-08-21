package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.dto.ProductResponse;
import com.berkaykomur.backend.dto.ScrapperResult;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScrapperServiceImpl implements ScrapperService {
     private final List<Scrapper> scrappers;
     private final ProductRepository productRepository;
     private final ProductMapper productMapper;


     @Transactional
     @Override
     public ProductResponse executeScrapping(String productUrl,boolean forceRefresh){
         Optional<Product> product=productRepository.findProductIncludingDeleted(productUrl);
//                 .map(existingProduct-> {
//                     productMapper.updateProductFromDto(productResponse, existingProduct);
//                     return existingProduct;
//                 })
//                 .orElseGet(()-> {
//                     return productMapper.toProduct(productResponse);
//                 });
         if(product.isPresent()&&!forceRefresh){
             productRepository.restoreProduct(product.get().getId());
            return productMapper.toProductResponse(product.get());
         }
         Scrapper scrapper=getScrapper(productUrl);
         ScrapperResult scrapperResponse= scrapper.scrap(productUrl);
         Product savedProduct;
       
         if(product.isPresent()){
             Product existingProduct=product.get();
            productMapper.updateProductFromDto(scrapperResponse,existingProduct);
            savedProduct=productRepository.save(existingProduct);
         }
         else {
             savedProduct=productRepository.save(productMapper.toProduct(scrapperResponse));
         }
         return productMapper.toProductResponse(savedProduct);
     }

     @Override
     public Scrapper getScrapper(String productUrl){
         return scrappers.stream()
                 .filter(s->s.supports(productUrl))
                 .findFirst()
                 .orElseThrow(()-> new UnspportedMarketPlaceException("URL'yi destekleyen site bulunamadı: "+productUrl));

     }

}
