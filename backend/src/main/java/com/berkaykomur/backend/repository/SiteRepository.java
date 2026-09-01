package com.berkaykomur.backend.repository;

import com.berkaykomur.backend.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SiteRepository extends JpaRepository<Site, Long> {
    List<Site> findAll();


}
