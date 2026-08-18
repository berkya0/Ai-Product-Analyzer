package com.berkaykomur.backend.mapper;

import com.berkaykomur.backend.dto.AnalysisHighLightResult;
import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.model.Analysis;
import com.berkaykomur.backend.model.AnalysisHighlight;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnalysisHighLightMapper {

    @Mapping(target = "analysis",ignore = true)
    AnalysisHighlight toAnalysisHighlight(AnalysisHighLightResult analysisHighLightResult);
    AnalysisHighLightResult toAnalysisHighlightResult(AnalysisHighlight analysisHighlight);

    @Mapping(target = "analysis",ignore = true)
    List<AnalysisHighlight> toAnalysisHighlights(List<AnalysisHighLightResult> analysisHighLightResults);
    List<AnalysisHighLightResult> toAnalysisHighLightResults(List<AnalysisHighlight> analysisHighlights);
}
