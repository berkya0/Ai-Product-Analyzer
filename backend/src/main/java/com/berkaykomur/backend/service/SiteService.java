package com.berkaykomur.backend.service;

import com.berkaykomur.backend.dto.SiteCreateRequest;
import com.berkaykomur.backend.exception.SiteNotFoundException;
import com.berkaykomur.backend.model.Site;
import com.berkaykomur.backend.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;

    @Transactional
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

    @Transactional
    public void deleteSite(Long id) {
        if (!siteRepository.existsById(id)) {
            throw new SiteNotFoundException("Silinecek site bulunamadı! ID: " + id);
        }
        siteRepository.deleteById(id);
    }
}