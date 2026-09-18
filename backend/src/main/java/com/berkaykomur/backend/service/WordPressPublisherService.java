package com.berkaykomur.backend.service;

import com.berkaykomur.backend.dto.ProductAnalysisCombinedResponse;

public interface WordPressPublisherService {
    String publish(Long siteId, String customTitle, String status, ProductAnalysisCombinedResponse combinedData, Long userId);
}
