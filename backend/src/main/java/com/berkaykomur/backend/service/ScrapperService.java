package com.berkaykomur.backend.service;

import com.berkaykomur.backend.dto.ProductResponse;

public interface ScrapperService {
    ProductResponse executeScrapping(String url);
}
