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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final AnalysisRepository analysisRepository;

    @Override
    public DashboardResponse getDashboard(Long userId){
        long totalAnalysis = analysisRepository.countByProduct_User_Id(userId);
        long successfulAnalysis = analysisRepository.countByProduct_User_IdAndStatus(userId, Status.SUCCESS);
        long failedAnalysis = analysisRepository.countByProduct_User_IdAndStatus(userId, Status.FAILED);
        long followingAnalysis = analysisRepository.countByProduct_IsFollowingAndProduct_User_Id(true, userId);

        return new  DashboardResponse(totalAnalysis,successfulAnalysis,failedAnalysis,followingAnalysis);
    }

    @Override
    public Page<DashboardProductsResponse> getProducts(int page, int size,Long userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Analysis> analyses = analysisRepository.findAllByProduct_User_IdOrderByCreatedAtDesc(pageable,userId);

        return analyses.map(analysis -> {
            Product product = analysis.getProduct();

            return new DashboardProductsResponse(
                    product.getId(),
                    product.getName(),
                    product.getImageUrl(),
                    product.getProductUrl(),
                    analysis.getAiScore(),
                    product.isFollowing(),
                    analysis.getStatus(),
                    product.getUpdatedAt()
            );
        });
    }
}
