package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.dto.DashboardProductsResponse;
import com.berkaykomur.backend.dto.DashboardResponse;
import com.berkaykomur.backend.model.Analysis;
import com.berkaykomur.backend.model.Product;
import com.berkaykomur.backend.model.Status;
import com.berkaykomur.backend.repository.AnalysisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private AnalysisRepository analysisRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private Long userId;
    private Product mockProduct;
    private Analysis mockAnalysis;

    @BeforeEach
    void setUp() {
        userId = 1L;

        mockProduct = Product.builder()
                .id(100L)
                .name("Test Ürün")
                .imageUrl("https://example.com/image.jpg")
                .productUrl("https://example.com/product/100")
                .isFollowing(true)
                .build();

        mockAnalysis = Analysis.builder()
                .product(mockProduct)
                .aiScore(4.3)
                .status(Status.SUCCESS)
                .build();

    }

    @Test
    @DisplayName("getDashboard: Kullanıcı ID verildiğinde doğru DTO yanıtını döndürmeli")
    void getDashboard_WhenUserIdProvided_ShouldReturnDashboardResponse() {
        // ARRANGE (Hazırlık)

        DashboardResponse expectedResponse = new DashboardResponse(10L, 8L, 2L, 5L);
        when(analysisRepository.getDashboardStats(userId)).thenReturn(expectedResponse);

        // ACT (Eylem)
        DashboardResponse actualResponse = dashboardService.getDashboard(userId);

        // ASSERT (Doğrulama)
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.totalAnalysis(), actualResponse.totalAnalysis());
        assertEquals(expectedResponse.successfulAnalysis(), actualResponse.successfulAnalysis());
        assertEquals(expectedResponse.failedAnalysis(), actualResponse.failedAnalysis());
        assertEquals(expectedResponse.totalFollowedAnalysis(), actualResponse.totalFollowedAnalysis());

        verify(analysisRepository, times(1)).getDashboardStats(userId);
    }

    @Test
    @DisplayName("getProducts: Sayfalama parametreleri ile analiz listesini DTO sayfasına doğru dönüştürmeli")
    void getProducts_WhenPageAndSizeProvided_ShouldReturnMappedPage() {
        // ARRANGE (Hazırlık)
        int page = 0;
        int size = 10;
        LocalDateTime now = LocalDateTime.now();

        // 3. Repository'nin dönmesi için sahte Page yapısı oluşturma
        Page<Analysis> mockAnalysisPage = new PageImpl<>(List.of(mockAnalysis));

        when(analysisRepository.findAllByProduct_User_IdOrderByCreatedAtDesc(any(Pageable.class), eq(userId)))
                .thenReturn(mockAnalysisPage);

        // ACT (Eylem)
        Page<DashboardProductsResponse> result = dashboardService.getProducts(page, size, userId);

        // ASSERT (Doğrulama)
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        DashboardProductsResponse productResponse = result.getContent().get(0);
        assertEquals(mockProduct.getId(), productResponse.id());
        assertEquals(mockProduct.getName(), productResponse.name());
        assertEquals(mockProduct.getImageUrl(), productResponse.imageUrl());
        assertEquals(mockProduct.getProductUrl(), productResponse.productUrl());
        assertEquals(mockAnalysis.getAiScore(), productResponse.aiScore());
        assertEquals(mockProduct.isFollowing(), productResponse.isFollowing());
        assertEquals(mockAnalysis.getStatus(), productResponse.status());
        assertEquals(mockProduct.getUpdatedAt(), productResponse.updatedAt());

        // 4. Pageable parametresinin (Sort ve Page/Size) doğru gönderildiğini yakalama ve doğrulama
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(analysisRepository, times(1))
                .findAllByProduct_User_IdOrderByCreatedAtDesc(pageableCaptor.capture(), eq(userId));

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(page, capturedPageable.getPageNumber());
        assertEquals(size, capturedPageable.getPageSize());
        assertEquals(Sort.by(Sort.Direction.DESC, "id"), capturedPageable.getSort());
    }
}
