package com.berkaykomur.backend.service;

import com.berkaykomur.backend.dto.SiteCreateRequest;
import com.berkaykomur.backend.dto.SiteResponse;
import com.berkaykomur.backend.model.UserEntity;

import java.util.List;

public interface SiteService {
    void addSite(SiteCreateRequest request, UserEntity user );
    List<SiteResponse> getActiveSites(Long userId);
    void deleteSite(Long id,Long userId);
}
