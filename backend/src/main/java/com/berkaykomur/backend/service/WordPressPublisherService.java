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
import org.springframework.http.ResponseEntity;
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

    public String publish(Long siteId, String customTitle,String status,ProductAnalysisCombinedResponse combinedData) {

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new SiteNotFoundException("Site bulunamadı! ID: " + siteId));

        String htmlContent = htmlBuilderService.buildHtml(combinedData.product(), combinedData.analysis());
        String title = (customTitle != null && !customTitle.isBlank())
                ? customTitle
                : combinedData.product().name() + " AI Destekli Detaylı Analizi";
        String postStatus = (status != null && !status.isBlank()) ? status : "pending";

        WordPressPostRequest payload = new WordPressPostRequest(title, htmlContent, postStatus);

        String credentials = site.getUsername() + ":" + site.getAppPassword();
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + encodedCredentials;

        String wpEndpoint = site.getSiteUrl() + "/wp-json/wp/v2/posts";

        log.info("WordPress'e içerik gönderiliyor: {}", wpEndpoint);

        try {
            ResponseEntity<String> responseEntity = restClient.post()
                    .uri(wpEndpoint)
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toEntity(String.class);

            log.info("WordPress HTTP Status: {}", responseEntity.getStatusCode());
            log.info("WordPress Response Body: {}", responseEntity.getBody());

            if (responseEntity.getStatusCode().is3xxRedirection()) {
                throw new WordPressPublishException("WordPress Yönlendirme (301/302) yaptı! Veritabanındaki site URL'sinde 'https://' veya 'www.' eksik/fazla olabilir. Gidilen adres: " + wpEndpoint);
            }

            log.info("İçerik başarıyla yayınlandı!");
            return responseEntity.getBody();

        } catch (Exception e) {
            log.error("WordPress'e gönderim başarısız oldu: ", e);
            throw new WordPressPublishException("WordPress yayınlama hatası: " + e.getMessage(), e);
        }
    }
}