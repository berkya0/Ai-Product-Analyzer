package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.ai.AiAnalysis;
import com.berkaykomur.backend.dto.AnalysisHighLightResult;
import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.dto.FeatureSentimentResult;
import com.berkaykomur.backend.model.Analysis;
import com.berkaykomur.backend.model.AnalysisHighlight;
import com.berkaykomur.backend.scrapper.Scrapper;
import com.berkaykomur.backend.service.AiAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiAnalysisServiceImpl implements AiAnalysisService {

    private final AiAnalysis aiAnalysis;
    public void createAnalysis (Scrapper scrapper,String productUrl){
        AnalysisResult analysisResult=aiAnalysis.analyzeComments(scrapper,productUrl);
        if(analysisResult==null){
            return;
        }

        List<AnalysisHighLightResult> results=analysisResult.highlights();
        List<FeatureSentimentResult> featureSentimentResults=analysisResult.featureResults();


        Analysis analysis=Analysis.builder()
                .aiScore(analysisResult.aiScore())
                .summary(analysisResult.summary())
                .topNegativeComment(analysisResult.topNegativeComment())
                .topPositiveComment(analysisResult.topPositiveComment())
                .build();

    }
}
