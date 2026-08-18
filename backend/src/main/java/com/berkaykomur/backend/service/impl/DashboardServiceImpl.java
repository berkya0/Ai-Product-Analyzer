package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.dto.DashboardResponse;
import com.berkaykomur.backend.model.Status;
import com.berkaykomur.backend.repository.AnalysisRepository;
import com.berkaykomur.backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final AnalysisRepository analysisRepository;
    @Override
    public DashboardResponse getDashboard(){
        long totalAnalysis = analysisRepository.count();
        long successfulAnalysis = analysisRepository.countByStatus(Status.SUCCESS);
        long failedAnalysis = analysisRepository.countByStatus(Status.FAILED);
        long followingAnalysis=analysisRepository.countByProduct_IsFollowing(true);

        return new  DashboardResponse(totalAnalysis,successfulAnalysis,failedAnalysis,followingAnalysis);
    }
}
