package com.berkaykomur.backend.service;

import com.berkaykomur.backend.dto.SiteCreateRequest;
import com.berkaykomur.backend.model.Site;
import com.berkaykomur.backend.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;

    public void addSite(SiteCreateRequest request) {
        Site newSite = Site.builder()
                .siteName(request.siteName())
                .siteUrl(request.siteUrl())
                .username(request.username())
                .appPassword(request.appPassword())
                .build();

        siteRepository.save(newSite);
    }

    public List<Site> getActiveSites() {
        return siteRepository.findAll();
    }
}