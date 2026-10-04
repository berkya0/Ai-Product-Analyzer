package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.dto.CompareResults;
import com.berkaykomur.backend.exception.product.CategoryMismatchException;
import com.berkaykomur.backend.exception.product.ProductNotFoundException;
import com.berkaykomur.backend.mapper.AnalysisMapper;
import com.berkaykomur.backend.model.Analysis;
import com.berkaykomur.backend.model.Product;
import com.berkaykomur.backend.model.UserEntity;
import com.berkaykomur.backend.repository.AnalysisRepository;
import com.berkaykomur.backend.repository.ProductRepository;
import com.berkaykomur.backend.service.ScrapperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ScrapperService scrapperService;

    @Mock
    private AnalysisRepository analysisRepository;

    @Mock
    private AnalysisMapper analysisMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private final Long userId = 1L;
    private final Long productId = 10L;

    private Product mockProduct;
    private UserEntity mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new UserEntity();
        mockUser.setId(userId);

        mockProduct = new Product();
        mockProduct.setId(productId);
        mockProduct.setProductUrl("https://example.com/product/10");
        mockProduct.setFollowing(false);
        mockProduct.setCategory("Elektronik");
        mockProduct.setUser(mockUser);
    }


    @Test
    @DisplayName("setFollow - Ürün bulunduğunda: Takip durumu güncellenmeli ve kaydedilmeli")
    void setFollow_WhenProductExists_ShouldUpdateFollowingAndSave() {
        // ARRANGE
        when(productRepository.findByIdAndUser_Id(productId, userId))
                .thenReturn(Optional.of(mockProduct));

        // ACT
        productService.setFollow(productId, true, userId);

        // ASSERT
        assertTrue(mockProduct.isFollowing());
        verify(productRepository, times(1)).findByIdAndUser_Id(productId, userId);
        verify(productRepository, times(1)).save(mockProduct);
    }

    @Test
    @DisplayName("setFollow - Ürün bulunamadığında: ProductNotFoundException fırlatılmalı")
    void setFollow_WhenProductDoesNotExist_ShouldThrowProductNotFoundException() {
        // ARRANGE
        when(productRepository.findByIdAndUser_Id(productId, userId))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.setFollow(productId, true, userId)
        );

        assertEquals("Ürün bulunamadı veya bu işlem için yetkiniz yok!", exception.getMessage());
        verify(productRepository, never()).save(any());
    }


    @Test
    @DisplayName("updateFollowedProductPrices - Takip edilen ürün yoksa: İşlem sonlandırılmalı")
    void updateFollowedProductPrices_WhenNoFollowedProducts_ShouldReturnEarly() {
        // ARRANGE
        when(productRepository.findAllByIsFollowingIsTrue()).thenReturn(List.of());

        // ACT
        productService.updateFollowedProductPrices();

        // ASSERT
        verify(productRepository, times(1)).findAllByIsFollowingIsTrue();
        verifyNoInteractions(scrapperService);
    }

    @Test
    @DisplayName("updateFollowedProductPrices - Takip edilen ürünler varsa: Her ürün için kazıma çalıştırılmalı")
    void updateFollowedProductPrices_WhenFollowedProductsExist_ShouldExecuteScrapping() {
        // ARRANGE
        mockProduct.setFollowing(true);
        when(productRepository.findAllByIsFollowingIsTrue()).thenReturn(List.of(mockProduct));

        // ACT
        productService.updateFollowedProductPrices();

        // ASSERT
        verify(scrapperService, times(1))
                .executeScrapping(mockProduct.getProductUrl(), userId);
    }

    @Test
    @DisplayName("updateFollowedProductPrices - Scrapper hata verirse: Hata yakalanmalı ve akış kesilmemeli")
    void updateFollowedProductPrices_WhenScrapperThrowsException_ShouldCatchAndContinue() {
        // ARRANGE
        mockProduct.setFollowing(true);
        when(productRepository.findAllByIsFollowingIsTrue()).thenReturn(List.of(mockProduct));
        when(scrapperService.executeScrapping(anyString(), any()))
                .thenThrow(new RuntimeException("Kazıma hatası"));

        // ACT & ASSERT
        assertDoesNotThrow(() -> productService.updateFollowedProductPrices());
        verify(scrapperService, times(1))
                .executeScrapping(mockProduct.getProductUrl(), userId);
    }

    @Test
    @DisplayName("deleteProductDetailById - Ürün bulunduğunda: Silme metodu çağrılmalı")
    void deleteProductDetailById_WhenProductExists_ShouldDeleteProduct() {
        // ARRANGE
        when(productRepository.findByIdAndUser_Id(productId, userId))
                .thenReturn(Optional.of(mockProduct));

        // ACT
        productService.deleteProductDetailById(productId, userId);

        // ASSERT
        verify(productRepository, times(1)).findByIdAndUser_Id(productId, userId);
        verify(productRepository, times(1)).delete(mockProduct);
    }

    @Test
    @DisplayName("deleteProductDetailById - Ürün bulunamadığında: ProductNotFoundException fırlatılmalı")
    void deleteProductDetailById_WhenProductDoesNotExist_ShouldThrowProductNotFoundException() {
        // ARRANGE
        when(productRepository.findByIdAndUser_Id(productId, userId))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productService.deleteProductDetailById(productId, userId)
        );

        assertTrue(exception.getMessage().contains("Ürün idye göre bulunamadı: " + productId));
        verify(productRepository, never()).delete(any());
    }


    @Test
    @DisplayName("compareProducts - Ürün ID listesi null veya 2'den farklı sayıda ise: IllegalArgumentException fırlatılmalı")
    void compareProducts_WhenProductIdsNullOrNotTwo_ShouldThrowIllegalArgumentException() {
        // ACT & ASSERT // ??? bu ney
        assertThrows(IllegalArgumentException.class, () -> productService.compareProducts(null, userId));
        assertThrows(IllegalArgumentException.class, () -> productService.compareProducts(List.of(1L), userId));
        assertThrows(IllegalArgumentException.class, () -> productService.compareProducts(List.of(1L, 2L, 3L), userId));

        verifyNoInteractions(analysisRepository);
    }

    @Test
    @DisplayName("compareProducts - Bulunan analiz sayısı 2 değilse: ProductNotFoundException fırlatılmalı")
    void compareProducts_WhenAnalysesCountNotTwo_ShouldThrowProductNotFoundException() {
        // ARRANGE
        List<Long> productIds = List.of(10L, 20L);
        Analysis singleAnalysis = new Analysis();

        when(analysisRepository.findAllByProduct_IdInAndProduct_User_Id(productIds, userId))
                .thenReturn(List.of(singleAnalysis));

        // ACT & ASSERT
        assertThrows(
                ProductNotFoundException.class,
                () -> productService.compareProducts(productIds, userId)
        );

        verify(analysisMapper, never()).toCompareResults(any());
    }

    @Test
    @DisplayName("compareProducts - Ürün kategorileri eşleşmiyorsa: CategoryMismatchException fırlatılmalı")
    void compareProducts_WhenCategoriesMismatch_ShouldThrowCategoryMismatchException() {
        // ARRANGE
        List<Long> productIds = List.of(10L, 20L);

        Product product1 = createProductWithCategory("Elektronik");
        Product product2 = createProductWithCategory("Giyim");

        Analysis analysis1 = new Analysis();
        analysis1.setProduct(product1);

        Analysis analysis2 = new Analysis();
        analysis2.setProduct(product2);

        when(analysisRepository.findAllByProduct_IdInAndProduct_User_Id(productIds, userId))
                .thenReturn(List.of(analysis1, analysis2));

        // ACT & ASSERT
        CategoryMismatchException exception = assertThrows(
                CategoryMismatchException.class,
                () -> productService.compareProducts(productIds, userId)
        );

        assertTrue(exception.getMessage().contains("Farklı kategorideki ürünler karşılaştırılamaz!"));
        verify(analysisMapper, never()).toCompareResults(any());
    }

    @Test
    @DisplayName("compareProducts - İki ürün aynı kategorideyse: Başarıyla karşılaştırma sonuçları dönülmeli")
    void compareProducts_WhenValid_ShouldReturnCompareResults() {
        // ARRANGE
        List<Long> productIds = List.of(10L, 20L);

        Product product1 = createProductWithCategory("Elektronik");
        Product product2 = createProductWithCategory("Elektronik");

        Analysis analysis1 = new Analysis();
        analysis1.setProduct(product1);

        Analysis analysis2 = new Analysis();
        analysis2.setProduct(product2);

        CompareResults compareResult1 = CompareResults.builder().build();
        CompareResults compareResult2 = CompareResults.builder().build();

        when(analysisRepository.findAllByProduct_IdInAndProduct_User_Id(productIds, userId))
                .thenReturn(List.of(analysis1, analysis2));
        when(analysisMapper.toCompareResults(analysis1)).thenReturn(compareResult1);
        when(analysisMapper.toCompareResults(analysis2)).thenReturn(compareResult2);

        // ACT
        List<CompareResults> results = productService.compareProducts(productIds, userId);

        // ASSERT
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(compareResult1, results.get(0));
        assertEquals(compareResult2, results.get(1));

        verify(analysisRepository, times(1)).findAllByProduct_IdInAndProduct_User_Id(productIds, userId);
        verify(analysisMapper, times(2)).toCompareResults(any(Analysis.class));
    }


    private Product createProductWithCategory(String category) {
        Product product = new Product();
        product.setCategory(category);
        return product;
    }
}