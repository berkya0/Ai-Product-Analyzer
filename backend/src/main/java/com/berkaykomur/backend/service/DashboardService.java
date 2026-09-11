package com.berkaykomur.backend.service;

import com.berkaykomur.backend.dto.DashboardProductsResponse;
import com.berkaykomur.backend.dto.DashboardResponse;
import org.springframework.data.domain.Page;

public interface DashboardService {
    DashboardResponse getDashboard(Long userId);
    Page<DashboardProductsResponse> getProducts(int page, int size,Long userId);

}
