package com.berkaykomur.backend.controller;

import com.berkaykomur.backend.dto.DashboardProductsResponse;
import com.berkaykomur.backend.dto.DashboardResponse;
import com.berkaykomur.backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/dashboard")
@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/get-cards")
    public ResponseEntity<DashboardResponse> getDashboardStatueCards(){
        return ResponseEntity.ok(dashboardService.getDashboard());
    }

    @GetMapping("/get-products")
    public ResponseEntity<Page<DashboardProductsResponse>> getDashboardProducts(@RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam  int size ){
        return ResponseEntity.ok(dashboardService.getProducts(page, size));

    }

}
