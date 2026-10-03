package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.ai.AiAnalysis;
import com.berkaykomur.backend.dto.AnalysisResult;
import com.berkaykomur.backend.exception.analysis.AiAnalaysisNotFoundException;
import com.berkaykomur.backend.exception.product.ProductNotFoundException;
import com.berkaykomur.backend.mapper.AnalysisMapper;
import com.berkaykomur.backend.model.Analysis;
import com.berkaykomur.backend.model.Product;
import com.berkaykomur.backend.model.Status;
import com.berkaykomur.backend.model.UserEntity;
import com.berkaykomur.backend.repository.AnalysisRepository;
import com.berkaykomur.backend.repository.ProductRepository;
import com.berkaykomur.backend.scrapper.Scrapper;
import com.berkaykomur.backend.scrapper.impl.TrendyolScrapper;
import com.berkaykomur.backend.service.AiAnalysisService;
import com.berkaykomur.backend.util.AnalysisSaveHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiAnalysisServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AnalysisRepository analysisRepository;

    @Mock
    private AnalysisMapper analysisMapper;

    @Mock
    private AiAnalysis aiAnalysis;

    @Mock
    private AnalysisSaveHelper analysisSaveHelper;

    @Mock
    private Scrapper scrapper;

    @InjectMocks
    private AiAnalysisServiceImpl analysisService;

    private Product mockProduct;
    private Analysis mockAnalysis;
    private AnalysisResult mockAnalysisResult;
    private final Long productId = 1L;
    private final Long userId = 10L;

    @BeforeEach
    void setUp() {
        mockProduct = new Product();
        mockProduct.setId(productId);
        mockProduct.setProductUrl("https://example.com/product/1");

        mockAnalysis = new Analysis();
        mockAnalysis.setStatus(Status.SUCCESS);

        mockAnalysisResult = mock(AnalysisResult.class);
    }

    @Test
    @DisplayName("createAnalysis: Ürün bulunamadığında ProductNotFoundException fırlatmalı")
    void createAnalysis_WhenProductNotFound_ShouldThrowProductNotFoundException() {
        when(productRepository.findByIdAndUser_Id(productId, userId)).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> analysisService.createAnalysis(scrapper, productId, false, userId)
        );

        assertEquals("Id'ye göre ürün bulunamadı: " + productId, exception.getMessage());
        verifyNoInteractions(analysisRepository, aiAnalysis, analysisMapper, analysisSaveHelper);
    }

    @Test
    @DisplayName("createAnalysis: Mevcut başarılı analiz var ve forceRefresh=false ise direkt önbellekten dönmeli")
    void createAnalysis_WhenExistingSuccessAnalysisAndNoForceRefresh_ShouldReturnCachedResult() {

        when(productRepository.findByIdAndUser_Id(productId, userId)).thenReturn(Optional.of(mockProduct));
        when(analysisRepository.getAnalysisByProduct_IdAndProduct_User_Id(productId, userId))
                .thenReturn(Optional.of(mockAnalysis));
        when(analysisMapper.toAnalysisResult(mockAnalysis)).thenReturn(mockAnalysisResult);

        AnalysisResult result = analysisService.createAnalysis(scrapper, productId, false, userId);

        assertNotNull(result);
        assertEquals(mockAnalysisResult, result);

        verify(analysisMapper, times(1)).toAnalysisResult(mockAnalysis);
        verifyNoInteractions(aiAnalysis, analysisSaveHelper);
    }

    @Test
    @DisplayName("createAnalysis: AI analizi null döndüğünde AiAnalaysisNotFoundException fırlatmalı")
    void createAnalysis_WhenAiAnalysisReturnsNull_ShouldThrowException() {

        when(productRepository.findByIdAndUser_Id(productId, userId)).thenReturn(Optional.of(mockProduct));
        when(analysisRepository.getAnalysisByProduct_IdAndProduct_User_Id(productId, userId))
                .thenReturn(Optional.empty());
        when(aiAnalysis.analyzeComments(scrapper, mockProduct.getProductUrl())).thenReturn(null);

        AiAnalaysisNotFoundException exception = assertThrows(
                AiAnalaysisNotFoundException.class,
                () -> analysisService.createAnalysis(scrapper, productId, false, userId)
        );

        assertTrue(exception.getMessage().contains(mockProduct.getProductUrl()));
        verifyNoInteractions(analysisSaveHelper);
    }

    @Test
    @DisplayName("createAnalysis: forceRefresh=true olduğunda AI analizi yeniden çalıştırıp kaydetmeli")
    void createAnalysis_WhenForceRefreshIsTrue_ShouldRunAiAnalysisAndSave() {
        when(productRepository.findByIdAndUser_Id(productId, userId)).thenReturn(Optional.of(mockProduct));
        when(analysisRepository.getAnalysisByProduct_IdAndProduct_User_Id(productId, userId))
                .thenReturn(Optional.of(mockAnalysis));
        when(aiAnalysis.analyzeComments(scrapper, mockProduct.getProductUrl())).thenReturn(mockAnalysisResult);
        when(analysisSaveHelper.saveAnalysisResult(eq(mockProduct), any(), eq(mockAnalysisResult)))
                .thenReturn(mockAnalysisResult);

        AnalysisResult result = analysisService.createAnalysis(scrapper, productId, true, userId);

        assertNotNull(result);
        assertEquals(mockAnalysisResult, result);

        verifyNoInteractions(analysisMapper);
        verify(aiAnalysis, times(1)).analyzeComments(scrapper, mockProduct.getProductUrl());
        verify(analysisSaveHelper, times(1)).saveAnalysisResult(mockProduct, Optional.of(mockAnalysis), mockAnalysisResult);
    }
}