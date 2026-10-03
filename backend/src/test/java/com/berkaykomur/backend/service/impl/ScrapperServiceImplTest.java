package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.dto.ProductResponse;
import com.berkaykomur.backend.dto.ScrapperResult;
import com.berkaykomur.backend.exception.analysis.UnspportedMarketPlaceException;
import com.berkaykomur.backend.exception.user.UserNotFoundException;
import com.berkaykomur.backend.mapper.ProductMapper;
import com.berkaykomur.backend.model.Product;
import com.berkaykomur.backend.model.UserEntity;
import com.berkaykomur.backend.repository.ProductRepository;
import com.berkaykomur.backend.repository.UserRepository;
import com.berkaykomur.backend.scrapper.Scrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScrapperServiceImplTest {

    @Mock
    private Scrapper scrapper;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private UserRepository userRepository;

    private ScrapperServiceImpl scrapperService;

    private final String productUrl = "https://example.com/product/123";
    private final Long userId = 1L;

    private ScrapperResult scrapperResult;
    private Product newProduct;
    private Product existingProduct;
    private UserEntity user;
    private ProductResponse expectedResponse;

    @BeforeEach
    void setUp() {
        scrapperService = new ScrapperServiceImpl(
                List.of(scrapper),
                productRepository,
                productMapper,
                userRepository
        );

        scrapperResult = createDummyScrapperResult();

        newProduct = new Product();
        newProduct.setId(10L);

        existingProduct = new Product();
        existingProduct.setId(5L);

        user = new UserEntity();
        user.setId(userId);

        expectedResponse = createDummyProductResponse();
    }

    @Test
    @DisplayName("executeScrapping - Ürün DB'de yoksa: Kazınmalı, kullanıcıya atanmalı ve yeni ürün kaydedilmeli")
    void executeScrapping_WhenProductDoesNotExist_ShouldCreateAndSaveProduct() {

        lenient().when(scrapper.supports(productUrl)).thenReturn(true);
        when(productRepository.findProductIncludingDeletedAndUser_Id(productUrl, userId))
                .thenReturn(Optional.empty());
        when(scrapper.scrap(productUrl)).thenReturn(scrapperResult);
        when(productMapper.toProduct(scrapperResult)).thenReturn(newProduct);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.save(newProduct)).thenReturn(newProduct);
        when(productMapper.toProductResponse(newProduct)).thenReturn(expectedResponse);

        ProductResponse actualResponse = scrapperService.executeScrapping(productUrl, false, userId);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);

        verify(userRepository, times(1)).findById(userId);
        verify(productRepository, times(1)).save(newProduct);
        verify(productMapper, times(1)).toProductResponse(newProduct);
        verify(productRepository, never()).restoreProduct(any());
    }

    @Test
    @DisplayName("executeScrapping - Ürün DB'de yok ama Kullanıcı bulunamadıysa: UserNotFoundException fırlatılmalı")
    void executeScrapping_WhenProductDoesNotExistAndUserNotFound_ShouldThrowUserNotFoundException() {
        // ARRANGE / GIVEN
        lenient().when(scrapper.supports(productUrl)).thenReturn(true);
        when(productRepository.findProductIncludingDeletedAndUser_Id(productUrl, userId))
                .thenReturn(Optional.empty());
        when(scrapper.scrap(productUrl)).thenReturn(scrapperResult);
        when(productMapper.toProduct(scrapperResult)).thenReturn(newProduct);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> scrapperService.executeScrapping(productUrl, false, userId)
        );

        assertTrue(exception.getMessage().contains("Kullanıcı bulunamadı id: " + userId));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("executeScrapping - Ürün DB'de var ve forceRefresh=true ise: Restore edilmeli, yeniden kazınıp güncellenmeli")
    void executeScrapping_WhenProductExistsAndForceRefreshIsTrue_ShouldRestoreAndScrapAndUpdateProduct() {
        // ARRANGE / GIVEN
        lenient().when(scrapper.supports(productUrl)).thenReturn(true);
        when(productRepository.findProductIncludingDeletedAndUser_Id(productUrl, userId))
                .thenReturn(Optional.of(existingProduct));
        when(scrapper.scrap(productUrl)).thenReturn(scrapperResult);
        when(productMapper.toProductResponse(existingProduct)).thenReturn(expectedResponse);

        // ACT / WHEN
        ProductResponse actualResponse = scrapperService.executeScrapping(productUrl, true, userId);

        // ASSERT / THEN
        assertEquals(expectedResponse, actualResponse);
        verify(productRepository, times(1)).restoreProduct(existingProduct.getId());
        verify(scrapper, times(1)).scrap(productUrl);
        verify(productMapper, times(1)).updateProductFromDto(scrapperResult, existingProduct);
        verify(productMapper, times(1)).toProductResponse(existingProduct);
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("executeScrapping - Ürün DB'de var ve forceRefresh=false ise: Yeniden kazıma yapılmadan restore edilip dönülmeli")
    void executeScrapping_WhenProductExistsAndForceRefreshIsFalse_ShouldRestoreWithoutScrapping() {
        // ARRANGE / GIVEN
        when(productRepository.findProductIncludingDeletedAndUser_Id(productUrl, userId))
                .thenReturn(Optional.of(existingProduct));
        when(productMapper.toProductResponse(existingProduct)).thenReturn(expectedResponse);

        // ACT / WHEN
        ProductResponse actualResponse = scrapperService.executeScrapping(productUrl, false, userId);

        // ASSERT / THEN
        assertEquals(expectedResponse, actualResponse);
        verify(productRepository, times(1)).restoreProduct(existingProduct.getId());
        verify(scrapper, never()).scrap(any());
        verify(productMapper, never()).updateProductFromDto(any(), any());
        verify(productMapper, times(1)).toProductResponse(existingProduct);
    }

    @Test
    @DisplayName("getScrapper - URL'yi destekleyen scrapper varsa: İlgili scrapper dönmeli")
    void getScrapper_WhenSupportedScrapperExists_ShouldReturnScrapper() {
        // ARRANGE / GIVEN
        when(scrapper.supports(productUrl)).thenReturn(true);

        // ACT / WHEN
        Scrapper result = scrapperService.getScrapper(productUrl);

        // ASSERT / THEN
        assertNotNull(result);
        assertEquals(scrapper, result);
        verify(scrapper, times(1)).supports(productUrl);
    }

    @Test
    @DisplayName("getScrapper - URL'yi destekleyen scrapper yoksa: UnspportedMarketPlaceException fırlatılmalı")
    void getScrapper_WhenNoSupportedScrapperExists_ShouldThrowUnspportedMarketPlaceException() {
        // ARRANGE / GIVEN
        when(scrapper.supports(productUrl)).thenReturn(false);

        // ACT & ASSERT / WHEN & THEN
        UnspportedMarketPlaceException exception = assertThrows(
                UnspportedMarketPlaceException.class,
                () -> scrapperService.getScrapper(productUrl)
        );

        assertTrue(exception.getMessage().contains("URL'yi destekleyen site bulunamadı: " + productUrl));
    }

    private ProductResponse createDummyProductResponse() {
        return ProductResponse.builder()
                .id(1L)
                .name("Test Ürün")
                .imageUrl("https://example.com/image.jpg")
                .productUrl("https://example.com/product/123")
                .rating(4.5)
                .price(new BigDecimal("299.99"))
                .reviewCount(150)
                .ratingCount(200)
                .isFollowing(true)
                .category("Elektronik")
                .build();
    }

    private ScrapperResult createDummyScrapperResult() {
        return ScrapperResult.builder()
                .id(1L)
                .name("Test Kazınan Ürün")
                .imageUrl("https://example.com/image.jpg")
                .productUrl("https://example.com/product/123")
                .rating(4.5)
                .price(new BigDecimal("299.99"))
                .reviewCount(150)
                .ratingCount(200)
                .category("Elektronik")
                .build();
    }
}