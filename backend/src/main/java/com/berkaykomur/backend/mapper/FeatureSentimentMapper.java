package com.berkaykomur.backend.mapper;

import com.berkaykomur.backend.dto.FeatureSentimentResult;
import com.berkaykomur.backend.model.FeatureSentiment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FeatureSentimentMapper {

    @Mapping(target = "analysis",ignore = true)
    List<FeatureSentiment> toFeatureSentiments(List<FeatureSentimentResult> featureSentimentResults);
}
