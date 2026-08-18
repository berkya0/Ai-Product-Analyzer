package com.berkaykomur.backend.controller;

import com.berkaykomur.backend.dto.DashboardResponse;
import com.berkaykomur.backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

}
