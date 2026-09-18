package com.berkaykomur.backend.util;
import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.exception.analysis.AiAnalaysisNotFoundException;
import com.berkaykomur.backend.mapper.AnalysisMapper;
import com.berkaykomur.backend.model.Analysis;
import com.berkaykomur.backend.model.Product;
import com.berkaykomur.backend.model.Status;
import com.berkaykomur.backend.repository.AnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AnalysisSaveHelper {

    private final AnalysisRepository analysisRepository;
    private final AnalysisMapper analysisMapper;

    @Transactional
    public AnalysisResult saveAnalysisResult(Product product, Optional<Analysis> existingAnalysisOp, AnalysisResult analysisResult) {
        if (existingAnalysisOp.isEmpty()) {
            throw new AiAnalaysisNotFoundException("Analiz akışında hata: PENDING analiz yok");
        }
        Analysis analysisEntity = existingAnalysisOp.get();
        analysisMapper.updateAnalysisFromDto(analysisResult, analysisEntity);
        analysisEntity.setStatus(Status.SUCCESS);
        analysisEntity.setProduct(product);

        Analysis savedAnalysis = analysisRepository.save(analysisEntity);
        return analysisMapper.toAnalysisResult(savedAnalysis);
    }
}