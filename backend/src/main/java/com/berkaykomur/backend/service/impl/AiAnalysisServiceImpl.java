package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.ai.AiAnalysis;
import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.exception.ProductNotFoundException;
import com.berkaykomur.backend.mapper.AnalysisHighLightMapper;
import com.berkaykomur.backend.mapper.AnalysisMapper;
import com.berkaykomur.backend.mapper.FeatureSentimentMapper;
import com.berkaykomur.backend.model.*;
import com.berkaykomur.backend.repository.AnalysisRepository;
import com.berkaykomur.backend.repository.ProductRepository;
import com.berkaykomur.backend.scrapper.Scrapper;
import com.berkaykomur.backend.service.AiAnalysisService;
import com.berkaykomur.backend.service.ScrapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AiAnalysisServiceImpl implements AiAnalysisService {

    private final AiAnalysis aiAnalysis;
    private final AnalysisHighLightMapper analysisHighLightMapper;
    private final FeatureSentimentMapper featureSentimentMapper;
    private final AnalysisMapper analysisMapper;
    private final AnalysisRepository analysisRepository;
    private final ScrapperService scrapperService;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public AnalysisResult createAnalysis (Long productId) {
        Product product=productRepository.findById(productId)
                .orElseThrow(()->new ProductNotFoundException("Id'ye göre ürün bulunamadı: "+productId));
        Optional<Analysis> analysis=analysisRepository.getAnalysisByProduct(product);

        if(analysis.isPresent()){
            return analysisMapper.toAnalysisResult(analysis.get());
        }

        Scrapper scrapper=scrapperService.getScrapper(product.getProductUrl());
        String productUrl=product.getProductUrl();

        AnalysisResult analysisResult=aiAnalysis.analyzeComments(scrapper,productUrl);
        if(analysisResult==null ){
            //hatalı durumda
            Analysis failedAnalysis=Analysis.builder()
                    .product(product)
                    .status(Status.FAILED)
                    .build();
            analysisRepository.save(failedAnalysis);
            return analysisMapper.toAnalysisResult(failedAnalysis);
        }
        List<AnalysisHighlight> analysisHighlightList=analysisHighLightMapper.toAnalysisHighlights(analysisResult.highlights());
        List<FeatureSentiment> featureSentiments=featureSentimentMapper.toFeatureSentiments(analysisResult.featureResults());
        Analysis analysiss=analysisMapper.toAnalysis(analysisResult);
        analysisHighlightList.forEach(analysiss::addHighlight);
        featureSentiments.forEach(analysiss::addFeatureSentiment);
        analysiss.setProduct(product);
        analysiss.setStatus(Status.SUCCESS);
        analysisRepository.save(analysiss);
        return analysisResult;

    }


}
