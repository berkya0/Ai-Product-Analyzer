package com.berkaykomur.backend.service;

import com.berkaykomur.backend.dto.SiteCreateRequest;
import com.berkaykomur.backend.dto.SiteResponse;
import com.berkaykomur.backend.exception.SiteNotFoundException;
import com.berkaykomur.backend.mapper.SiteMapper;
import com.berkaykomur.backend.model.Site;
import com.berkaykomur.backend.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SiteService {

    private final SiteRepository siteRepository;
    private final SiteMapper siteMapper;

    @Transactional
    public void addSite(SiteCreateRequest request) {
        log.info("Yönlendirilecek yeni site ekleme isteği alındı siteURL: {}",request.siteUrl());
        Site newSite = Site.builder()
                .siteName(request.siteName())
                .siteUrl(request.siteUrl())
                .username(request.username())
                .appPassword(request.appPassword())
                .build();

        siteRepository.save(newSite);
        log.info("Site başarıyla eklendi! siteURL: {}",request.siteUrl());
    }

    public List<SiteResponse> getActiveSites() {

        List<Site> siteList= siteRepository.findAll();
        return siteMapper.siteToSiteResponseList(siteList);
    }

    @Transactional
    public void deleteSite(Long id) {
        if (!siteRepository.existsById(id)) {
            throw new SiteNotFoundException("Silinecek site bulunamadı! ID: " + id);
        }
        siteRepository.deleteById(id);
    }
}