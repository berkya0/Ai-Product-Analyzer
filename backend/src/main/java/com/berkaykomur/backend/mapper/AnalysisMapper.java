package com.berkaykomur.backend.mapper;

import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.model.Analysis;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring",
        uses = {
                AnalysisHighLightMapper.class,
                FeatureSentimentMapper.class,
                ProductMapper.class,
        })
public interface AnalysisMapper {

    @Mapping(target = "id",ignore = true)
    @Mapping(target = "product",ignore = true)
    @Mapping(source = "featureResults", target ="featureSentiments")
    Analysis toAnalysis(AnalysisResult analysisResult);

    @AfterMapping
    default void setParentReferences(@MappingTarget Analysis analysis) {

        if (analysis.getHighlights() != null) {
            analysis.getHighlights().forEach(highlight -> highlight.setAnalysis(analysis));
        }

        if (analysis.getFeatureSentiments() != null) {
            analysis.getFeatureSentiments().forEach(sentiment -> sentiment.setAnalysis(analysis));
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    void updateAnalysisFromDto(AnalysisResult result, @MappingTarget Analysis analysis);

    @Mapping(source = "featureSentiments", target = "featureResults")
    AnalysisResult toAnalysisResult(Analysis analysis);

}
