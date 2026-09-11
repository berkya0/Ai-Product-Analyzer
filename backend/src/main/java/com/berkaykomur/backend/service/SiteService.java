package com.berkaykomur.backend.service;

import com.berkaykomur.backend.dto.SiteCreateRequest;
import com.berkaykomur.backend.dto.SiteResponse;
import com.berkaykomur.backend.exception.SiteNotFoundException;
import com.berkaykomur.backend.mapper.SiteMapper;
import com.berkaykomur.backend.model.Site;
import com.berkaykomur.backend.model.UserEntity;
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
    public void addSite(SiteCreateRequest request, UserEntity user ) {
        log.info("Yönlendirilecek yeni site ekleme isteği alındı siteURL: {}",request.siteUrl());
        Site newSite = Site.builder()
                .siteName(request.siteName())
                .siteUrl(request.siteUrl())
                .username(request.username())
                .appPassword(request.appPassword())
                .user(user)
                .build();
        siteRepository.save(newSite);
        log.info("Site başarıyla eklendi! siteURL: {}",request.siteUrl());
    }

    public List<SiteResponse> getActiveSites(Long userId) {

        List<Site> siteList= siteRepository.findAllByUser_Id(userId);
        return siteMapper.siteToSiteResponseList(siteList);
    }

    @Transactional
    public void deleteSite(Long id,Long userId) {
        if (!siteRepository.existsByIdAndUser_Id(id,userId)) {
            throw new SiteNotFoundException("Silinecek site bulunamadı! ID: " + id);
        }
        siteRepository.deleteById(id);
    }
}