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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {
    private final AnalysisRepository analysisRepository;

    @Override
    public DashboardResponse getDashboard(Long userId) {
        return analysisRepository.getDashboardStats(userId);
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
