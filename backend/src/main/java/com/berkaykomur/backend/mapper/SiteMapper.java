package com.berkaykomur.backend.mapper;

import com.berkaykomur.backend.dto.SiteResponse;
import com.berkaykomur.backend.model.Site;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SiteMapper {

    SiteResponse siteToSiteResponse(Site site);
    List<SiteResponse> siteToSiteResponseList(List<Site> sites);
}
