package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.dto.DashboardProductsResponse;
import com.berkaykomur.backend.dto.DashboardResponse;
import com.berkaykomur.backend.model.Analysis;
import com.berkaykomur.backend.model.Product;
import com.berkaykomur.backend.model.Status;
import com.berkaykomur.backend.repository.AnalysisRepository;
import com.berkaykomur.backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    @Override
    public Page<DashboardProductsResponse> getProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Analysis> analyses = analysisRepository.findAll(pageable);

        return analyses.map(analysis -> {
            Product product = analysis.getProduct();

            return new DashboardProductsResponse(
                    product.getId(),
                    product.getName(),
                    product.getImageUrl(),
                    analysis.getAiScore(),
                    product.isFollowing(),
                    analysis.getStatus(),
                    product.getUpdatedAt()
            );
        });
    }
}
