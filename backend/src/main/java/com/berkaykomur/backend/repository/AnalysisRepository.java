package com.berkaykomur.backend.repository;

import com.berkaykomur.backend.model.Analysis;
import com.berkaykomur.backend.model.Product;
import com.berkaykomur.backend.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis,Long> {

    Optional<Analysis> getAnalysisByProduct_Id(Long productId);

    Optional<Analysis> getAnalysisByProduct_IdAndProduct_User_Id(Long productId, Long userId);
    Optional<Analysis> findFirstByProduct_User_IdOrderByCreatedAtDesc(Long userId);

    Page<Analysis> findAllByProduct_User_IdOrderByCreatedAtDesc(Pageable pageable, Long userId);

    long countByProduct_User_Id(Long userId);

    long countByProduct_User_IdAndStatus(Long userId, Status status);

    long countByProduct_IsFollowingAndProduct_User_Id(boolean isFollowing, Long userId);

    List<Analysis> findAllByProduct_IdInAndProduct_User_Id(List<Long> productIds, Long userId);
}
