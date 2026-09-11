package com.berkaykomur.backend.repository;

import com.berkaykomur.backend.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SiteRepository extends JpaRepository<Site, Long> {
    List<Site> findAllByUser_Id(Long userId);
    boolean existsByIdAndUser_Id(Long siteId,Long userId);
    Optional<Site> findByIdAndUser_Id(Long siteId, Long userId);



}
