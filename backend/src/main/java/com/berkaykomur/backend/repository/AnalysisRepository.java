package com.berkaykomur.backend.repository;

import com.berkaykomur.backend.model.Analysis;
import com.berkaykomur.backend.model.Product;
import com.berkaykomur.backend.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis,Long> {
    Optional<Analysis> getAnalysisByProduct(Product product);
    long countByStatus(Status status);
    long countByProduct_IsFollowing(boolean isFollowing);
    Optional<Analysis> getAnalysisByProduct_Id(Long productİd);



}
