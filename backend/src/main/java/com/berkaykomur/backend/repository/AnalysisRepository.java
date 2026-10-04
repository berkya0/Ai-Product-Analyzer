package com.berkaykomur.backend.repository;

import com.berkaykomur.backend.dto.DashboardResponse;
import com.berkaykomur.backend.model.Analysis;
import com.berkaykomur.backend.model.Product;
import com.berkaykomur.backend.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis,Long> {

    Optional<Analysis> getAnalysisByProduct_Id(Long productId);

    Optional<Analysis> getAnalysisByProduct_IdAndProduct_User_Id(Long productId, Long userId);
    Optional<Analysis> findFirstByProduct_User_IdOrderByCreatedAtDesc(Long userId);

    Page<Analysis> findAllByProduct_User_IdOrderByCreatedAtDesc(Pageable pageable, Long userId);


    List<Analysis> findAllByProduct_IdInAndProduct_User_Id(List<Long> productIds, Long userId);

    @Query("""
        SELECT new com.berkaykomur.backend.dto.DashboardResponse(
            COUNT(a),
            COUNT(CASE WHEN a.status = com.berkaykomur.backend.model.Status.SUCCESS THEN 1 END),
            COUNT(CASE WHEN a.status = com.berkaykomur.backend.model.Status.FAILED THEN 1 END),
            COUNT(CASE WHEN a.product.isFollowing = true THEN 1 END)
        )
        FROM Analysis a
        WHERE a.product.user.id = :userId
    """)
    DashboardResponse getDashboardStats(@Param("userId") Long userId);
}
