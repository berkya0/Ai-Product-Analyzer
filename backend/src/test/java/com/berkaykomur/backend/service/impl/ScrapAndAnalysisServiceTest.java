package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.dto.ProductAnalysisCombinedResponse;
import com.berkaykomur.backend.dto.ProductResponse;
import com.berkaykomur.backend.exception.product.ProductNotFoundException;
import com.berkaykomur.backend.mapper.AnalysisMapper;
import com.berkaykomur.backend.mapper.ProductMapper;
import com.berkaykomur.backend.model.Analysis;
import com.berkaykomur.backend.model.Product;
import com.berkaykomur.backend.model.Status;
import com.berkaykomur.backend.repository.AnalysisRepository;
import com.berkaykomur.backend.repository.ProductRepository;
import com.berkaykomur.backend.scrapper.Scrapper;
import com.berkaykomur.backend.service.AiAnalysisService;
import com.berkaykomur.backend.service.ScrapperService;
import com.berkaykomur.backend.util.AnalysisSaveHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScrapAndAnalysisServiceTest {

    @Mock
    private ScrapperService scrapperService;

    @Mock
    private AiAnalysisService aiAnalysisService;

    @Mock
    private AnalysisRepository analysisRepository;

    @Mock
    private AnalysisMapper analysisMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AnalysisSaveHelper analysisSaveHelper;

    @Mock
    private Scrapper mockScrapper;

    @InjectMocks
    private ScrapAndAnalysisService scrapAndAnalysisService;

    private final Long userId = 1L;
    private final Long productId = 10L;
    private final String productUrl = "https://example.com/product/10";

    private Product mockProduct;
    private Analysis mockAnalysis;
    private ProductResponse mockProductResponse;
    private AnalysisResult mockAnalysisResult;

    @BeforeEach
    void setUp() {
        mockProduct = new Product();
        mockProduct.setId(productId);
        mockProduct.setProductUrl(productUrl);

        mockAnalysis = new Analysis();
        mockAnalysis.setId(100L);
        mockAnalysis.setProduct(mockProduct);
        mockAnalysis.setStatus(Status.PENDING);

        mockProductResponse = createDummyProductResponse();
        mockAnalysisResult = createDummyAnalysisResult();
    }

    @Test
    @DisplayName("startAsyncProcess - Başarılı akış: Scrapper alınıp AI analizi başlatılmalı")
    void startAsyncProcess_WhenSuccessful_ShouldCallAiAnalysisService() {
        // ARRANGE
        when(scrapperService.getScrapper(productUrl)).thenReturn(mockScrapper);

        // ACT
        scrapAndAnalysisService.startAsyncProcess(productId, productUrl, true, userId);

        // ASSERT
        verify(scrapperService, times(1)).getScrapper(productUrl);
        verify(aiAnalysisService, times(1)).createAnalysis(mockScrapper, productId, true, userId);
        verifyNoInteractions(analysisSaveHelper);
    }

    @Test
    @DisplayName("startAsyncProcess - Hata oluştuğunda: Exception yakalanmalı ve durum FAILED yapılmalı")
    void startAsyncProcess_WhenExceptionOccurs_ShouldUpdateStatusToFailed() {
        // ARRANGE
        when(scrapperService.getScrapper(productUrl)).thenThrow(new RuntimeException("Kazıma hatası"));

        // ACT & ASSERT
        assertDoesNotThrow(() -> scrapAndAnalysisService.startAsyncProcess(productId, productUrl, true, userId));
        verify(analysisSaveHelper, times(1)).updateStatusToFailed(productId, userId);
        verify(aiAnalysisService, never()).createAnalysis(any(), any(), anyBoolean(), any());
    }


    @Test
    @DisplayName("initiateAnalysis - Analiz yoksa: Yeni PENDING durumunda analiz oluşturulmalı")
    void initiateAnalysis_WhenNoExistingAnalysis_ShouldCreateAndSaveNewAnalysis() {
        // ARRANGE
        when(scrapperService.executeScrapping(productUrl, false, userId)).thenReturn(mockProductResponse);
        when(productRepository.findProductIncludingDeletedAndUser_Id(productUrl, userId))
                .thenReturn(Optional.of(mockProduct));
        when(analysisRepository.getAnalysisByProduct_Id(productId)).thenReturn(Optional.empty());

        // ACT
        Long resultId = scrapAndAnalysisService.initiateAnalysis(productUrl, false, userId);

        // ASSERT
        assertEquals(productId, resultId);
        verify(analysisRepository, times(1)).save(any(Analysis.class));
    }

    @Test
    @DisplayName("initiateAnalysis - Analiz zaten varsa: Yeni analiz kaydedilmeden ürün ID'si dönülmeli")
    void initiateAnalysis_WhenAnalysisAlreadyExists_ShouldNotSaveNewAnalysis() {
        // ARRANGE
        when(scrapperService.executeScrapping(productUrl, false, userId)).thenReturn(mockProductResponse);
        when(productRepository.findProductIncludingDeletedAndUser_Id(productUrl, userId))
                .thenReturn(Optional.of(mockProduct));
        when(analysisRepository.getAnalysisByProduct_Id(productId)).thenReturn(Optional.of(mockAnalysis));

        // ACT
        Long resultId = scrapAndAnalysisService.initiateAnalysis(productUrl, false, userId);

        // ASSERT
        assertEquals(productId, resultId);
        verify(analysisRepository, never()).save(any());
    }

    @Test
    @DisplayName("initiateAnalysis - Ürün veritabanında bulunamazsa: ProductNotFoundException fırlatılmalı")
    void initiateAnalysis_WhenProductNotFound_ShouldThrowProductNotFoundException() {
        // ARRANGE
        when(scrapperService.executeScrapping(productUrl, false, userId)).thenReturn(mockProductResponse);
        when(productRepository.findProductIncludingDeletedAndUser_Id(productUrl, userId))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> scrapAndAnalysisService.initiateAnalysis(productUrl, false, userId)
        );

        assertTrue(exception.getMessage().contains("Ürün bilgileri çekilemedi :"));
        verify(analysisRepository, never()).save(any());
    }


    @Test
    @DisplayName("getLatestAnalysis - Son analiz bulunduğunda: Birleştirilmiş yanıt dönülmeli")
    void getLatestAnalysis_WhenAnalysisExists_ShouldReturnCombinedResponse() {
        // ARRANGE
        when(analysisRepository.findFirstByProduct_User_IdOrderByCreatedAtDesc(userId))
                .thenReturn(Optional.of(mockAnalysis));
        when(analysisMapper.toAnalysisResult(mockAnalysis)).thenReturn(mockAnalysisResult);
        when(productMapper.toProductResponse(mockProduct)).thenReturn(mockProductResponse);

        // ACT
        ProductAnalysisCombinedResponse response = scrapAndAnalysisService.getLatestAnalysis(userId);

        // ASSERT
        assertNotNull(response);
        verify(analysisMapper, times(1)).toAnalysisResult(mockAnalysis);
        verify(productMapper, times(1)).toProductResponse(mockProduct);
    }

    @Test
    @DisplayName("getLatestAnalysis - Hiç analiz yoksa: İçi null alanlardan oluşan yanıt dönülmeli")
    void getLatestAnalysis_WhenNoAnalysisExists_ShouldReturnNullCombinedResponse() {
        // ARRANGE
        when(analysisRepository.findFirstByProduct_User_IdOrderByCreatedAtDesc(userId))
                .thenReturn(Optional.empty());

        // ACT
        ProductAnalysisCombinedResponse response = scrapAndAnalysisService.getLatestAnalysis(userId);

        // ASSERT
        assertNotNull(response);
        verify(analysisMapper, never()).toAnalysisResult(any());
        verify(productMapper, never()).toProductResponse(any());
    }


    @Test
    @DisplayName("getAnalysisById - Ürün ID ile analiz bulunduğunda: Birleştirilmiş yanıt dönülmeli")
    void getAnalysisById_WhenAnalysisExists_ShouldReturnCombinedResponse() {
        // ARRANGE
        when(analysisRepository.getAnalysisByProduct_IdAndProduct_User_Id(productId, userId))
                .thenReturn(Optional.of(mockAnalysis));
        when(analysisMapper.toAnalysisResult(mockAnalysis)).thenReturn(mockAnalysisResult);
        when(productMapper.toProductResponse(mockProduct)).thenReturn(mockProductResponse);

        // ACT
        ProductAnalysisCombinedResponse response = scrapAndAnalysisService.getAnalysisById(productId, userId);

        // ASSERT
        assertNotNull(response);
        verify(analysisRepository, times(1)).getAnalysisByProduct_IdAndProduct_User_Id(productId, userId);
        verify(analysisMapper, times(1)).toAnalysisResult(mockAnalysis);
        verify(productMapper, times(1)).toProductResponse(mockProduct);
    }

    @Test
    @DisplayName("getAnalysisById - Analiz bulunamazsa: ProductNotFoundException fırlatılmalı")
    void getAnalysisById_WhenNotFound_ShouldThrowProductNotFoundException() {
        // ARRANGE
        when(analysisRepository.getAnalysisByProduct_IdAndProduct_User_Id(productId, userId))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> scrapAndAnalysisService.getAnalysisById(productId, userId)
        );

        assertTrue(exception.getMessage().contains("Ürün id ile bulunamadı: " + productId));
    }


    private ProductResponse createDummyProductResponse() {
        return ProductResponse.builder()
                .id(productId)
                .name("Test Ürün")
                .productUrl(productUrl)
                .price(new BigDecimal("100.0"))
                .build();
    }

    private AnalysisResult createDummyAnalysisResult() {
        return AnalysisResult.builder()
                .id(100L)
                .build();
    }
}