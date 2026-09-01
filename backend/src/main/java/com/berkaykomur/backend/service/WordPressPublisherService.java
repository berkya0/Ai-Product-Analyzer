package com.berkaykomur.backend.service;

import com.berkaykomur.backend.dto.ProductAnalysisCombinedResponse;
import com.berkaykomur.backend.dto.WordPressPostRequest;
import com.berkaykomur.backend.exception.SiteNotFoundException;
import com.berkaykomur.backend.exception.WordPressPublishException;
import com.berkaykomur.backend.model.Site;
import com.berkaykomur.backend.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordPressPublisherService {

    private final SiteRepository siteRepository;
    private final WordPressHtmlBuilderService htmlBuilderService;
    private final RestClient restClient = RestClient.create();

    public String publish(Long siteId, ProductAnalysisCombinedResponse combinedData) {

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new SiteNotFoundException("Site bulunamadı! ID: " + siteId));

        String htmlContent = htmlBuilderService.buildHtml(combinedData.product(), combinedData.analysis());
        String title = combinedData.product().name() + " AI Destekli Detaylı Analizi";

        WordPressPostRequest payload = new WordPressPostRequest(title, htmlContent, "draft");


        String credentials = site.getUsername() + ":" + site.getAppPassword();
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + encodedCredentials;

        String wpEndpoint = site.getSiteUrl() + "/wp-json/wp/v2/posts";

        log.info("WordPress'e içerik gönderiliyor: {}", wpEndpoint);

        try {
            String response = restClient.post()
                    .uri(wpEndpoint)
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(String.class);

            log.info("İçerik başarıyla yayınlandı!");
            return response; // WordPress'in döndürdüğü JSON (Oluşturulan yazının ID'si vb. içerir)

        } catch (Exception e) {
            log.error("WordPress'e gönderim başarısız oldu: ", e);
            throw new WordPressPublishException("WordPress yayınlama hatası: " + e.getMessage(),e);
        }
    }
}