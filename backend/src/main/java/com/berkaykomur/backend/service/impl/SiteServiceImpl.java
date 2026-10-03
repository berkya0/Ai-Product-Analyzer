package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.dto.SiteCreateRequest;
import com.berkaykomur.backend.dto.SiteResponse;
import com.berkaykomur.backend.exception.site.SiteNotFoundException;
import com.berkaykomur.backend.mapper.SiteMapper;
import com.berkaykomur.backend.model.Site;
import com.berkaykomur.backend.model.UserEntity;
import com.berkaykomur.backend.repository.SiteRepository;
import com.berkaykomur.backend.service.SiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SiteServiceImpl implements SiteService {

    private final SiteRepository siteRepository;
    private final SiteMapper siteMapper;

    @Transactional
    @Override
    public void addSite(SiteCreateRequest request, UserEntity user ) {
        log.info("Yeni site ekleme isteği alındı siteURL: {} userId: {}",request.siteUrl(),user.getId());
        Site newSite = siteMapper.createSiteRequest(request);
        newSite.setUser(user);
        siteRepository.save(newSite);
        log.info("Site başarıyla eklendi! siteURL: {} userId: {}",request.siteUrl(),user.getId());
    }

    @Override
    public List<SiteResponse> getActiveSites(Long userId) {

        List<Site> siteList= siteRepository.findAllByUser_Id(userId);
        return siteMapper.siteToSiteResponseList(siteList);
    }

    @Transactional
    @Override
    public void deleteSite(Long id,Long userId) {
        if (!siteRepository.existsByIdAndUser_Id(id,userId)) {
            throw new SiteNotFoundException("Silinecek site bulunamadı! ID: " + id);
        }
        siteRepository.deleteById(id);
        log.info("Site silme işlemi başarılı userId: {} siteId: {}",userId,id);
    }
}