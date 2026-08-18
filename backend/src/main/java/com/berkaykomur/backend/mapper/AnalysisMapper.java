package com.berkaykomur.backend.mapper;

import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.model.Analysis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = {
                AnalysisHighLightMapper.class,
                FeatureSentimentMapper.class
        })
public interface AnalysisMapper {

    @Mapping(target = "highlights",ignore = true)
    @Mapping(target = "featureSentiments",ignore = true)
    @Mapping(target = "product",ignore = true)
    Analysis toAnalysis(AnalysisResult analysisResult);

    @Mapping(source = "featureSentiments", target = "featureResults")
    AnalysisResult toAnalysisResult(Analysis analysis);

}
