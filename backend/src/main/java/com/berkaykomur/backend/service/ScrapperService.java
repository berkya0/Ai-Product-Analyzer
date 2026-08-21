package com.berkaykomur.backend.service;

import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.dto.ProductResponse;
import com.berkaykomur.backend.model.Product;
import com.berkaykomur.backend.scrapper.Scrapper;

public interface ScrapperService {
    ProductResponse executeScrapping(String url,boolean forceRefresh);
    Scrapper getScrapper(String productUrl);

}
