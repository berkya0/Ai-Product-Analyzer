package com.berkaykomur.backend.controller;

import com.berkaykomur.backend.dto.SiteCreateRequest;
import com.berkaykomur.backend.model.Site;
import com.berkaykomur.backend.service.SiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sites")
@RequiredArgsConstructor

public class SiteController {

    private final SiteService siteService;

    @PostMapping
    public ResponseEntity<String> createSite(@Valid @RequestBody SiteCreateRequest request) {
        siteService.addSite(request);
        return ResponseEntity.ok(request.siteName() + " başarıyla sisteme eklendi.");
    }

    @GetMapping
    public ResponseEntity<List<Site>> getActiveSites() {
        List<Site> sites = siteService.getActiveSites();
        return ResponseEntity.ok(sites);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSite(@PathVariable Long id) {
        siteService.deleteSite(id);
        return ResponseEntity.noContent().build();
    }
}