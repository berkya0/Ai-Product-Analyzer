package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.dto.ProductAnalysisCombinedResponse;
import com.berkaykomur.backend.dto.ProductResponse;
import com.berkaykomur.backend.exception.site.SiteNotFoundException;
import com.berkaykomur.backend.exception.site.WordPressPublishException;
import com.berkaykomur.backend.model.Site;
import com.berkaykomur.backend.repository.SiteRepository;
import com.berkaykomur.backend.util.WordPressHtmlBuilderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@ExtendWith(MockitoExtension.class)
class WordPressPublisherServiceImplTest {

    @Mock
    private SiteRepository siteRepository;

    @Mock
    private WordPressHtmlBuilderService htmlBuilderService;

    private WordPressPublisherServiceImpl publisherService;
    private MockRestServiceServer mockServer;

    private final Long userId = 1L;
    private final Long siteId = 10L;

    private Site mockSite;
    private ProductAnalysisCombinedResponse mockCombinedData;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.build();

        publisherService = new WordPressPublisherServiceImpl(siteRepository, htmlBuilderService, restClient);

        mockSite = new Site();
        mockSite.setId(siteId);
        mockSite.setUsername("testuser");
        mockSite.setAppPassword("testpass");
        mockSite.setSiteUrl("https://example.com");

        ProductResponse productResponse = ProductResponse.builder()
                .id(100L)
                .name("Test Ürün")
                .build();

        AnalysisResult analysisResult = AnalysisResult.builder()
                .id(200L)
                .build();

        mockCombinedData = new ProductAnalysisCombinedResponse(productResponse, analysisResult);
    }

    @Test
    @DisplayName("publish - Başarılı akış: Özel başlık ve durum ile içerik başarıyla yayınlanmalı")
    void publish_WhenSuccessfulWithCustomTitleAndStatus_ShouldReturnResponseBody() {
        String expectedResponseBody = "{\"id\": 123, \"status\": \"publish\"}";

        when(siteRepository.findByIdAndUser_Id(siteId, userId)).thenReturn(Optional.of(mockSite));
        when(htmlBuilderService.buildHtml(any(), any())).thenReturn("<h1>Test HTML</h1>");

        mockServer.expect(requestTo("https://example.com/wp-json/wp/v2/posts"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(expectedResponseBody, MediaType.APPLICATION_JSON));

        String result = publisherService.publish(siteId, "Özel Başlık", "publish", mockCombinedData, userId);

        assertEquals(expectedResponseBody, result);
        mockServer.verify();
    }

    @Test
    @DisplayName("publish - Başlık ve durum boş olduğunda: Varsayılan değerler kullanılmalı")
    void publish_WhenTitleAndStatusAreBlank_ShouldUseDefaultValues() {
        String expectedResponseBody = "{\"id\": 124, \"status\": \"pending\"}";

        when(siteRepository.findByIdAndUser_Id(siteId, userId)).thenReturn(Optional.of(mockSite));
        when(htmlBuilderService.buildHtml(any(), any())).thenReturn("<p>Test İçerik</p>");

        mockServer.expect(requestTo("https://example.com/wp-json/wp/v2/posts"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(expectedResponseBody, MediaType.APPLICATION_JSON));

        String result = publisherService.publish(siteId, "", null, mockCombinedData, userId);

        assertEquals(expectedResponseBody, result);
        mockServer.verify();
    }

    @Test
    @DisplayName("publish - Site veritabanında bulunamadığında: SiteNotFoundException fırlatılmalı")
    void publish_WhenSiteNotFound_ShouldThrowSiteNotFoundException() {
        when(siteRepository.findByIdAndUser_Id(siteId, userId)).thenReturn(Optional.empty());

        assertThrows(SiteNotFoundException.class, () ->
                publisherService.publish(siteId, "Başlık", "publish", mockCombinedData, userId)
        );
    }

    @Test
    @DisplayName("publish - WordPress yönlendirme (3xx) döndüğünde: WordPressPublishException fırlatılmalı")
    void publish_WhenResponseIsRedirection_ShouldThrowWordPressPublishException() {
        when(siteRepository.findByIdAndUser_Id(siteId, userId)).thenReturn(Optional.of(mockSite));
        when(htmlBuilderService.buildHtml(any(), any())).thenReturn("<html></html>");

        mockServer.expect(requestTo("https://example.com/wp-json/wp/v2/posts"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.MOVED_PERMANENTLY));

        WordPressPublishException exception = assertThrows(
                WordPressPublishException.class,
                () -> publisherService.publish(siteId, "Başlık", "publish", mockCombinedData, userId)
        );

        assertTrue(exception.getMessage().contains("WordPress Yönlendirme (301/302) yaptı!"));
    }

    @Test
    @DisplayName("publish - Sunucu hatası alındığında: WordPressPublishException fırlatılmalı")
    void publish_WhenServerError_ShouldThrowWordPressPublishException() {
        when(siteRepository.findByIdAndUser_Id(siteId, userId)).thenReturn(Optional.of(mockSite));
        when(htmlBuilderService.buildHtml(any(), any())).thenReturn("<html></html>");

        mockServer.expect(requestTo("https://example.com/wp-json/wp/v2/posts"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        WordPressPublishException exception = assertThrows(
                WordPressPublishException.class,
                () -> publisherService.publish(siteId, "Başlık", "publish", mockCombinedData, userId)
        );

        assertTrue(exception.getMessage().contains("WordPress yayınlama hatası:"));
    }
}