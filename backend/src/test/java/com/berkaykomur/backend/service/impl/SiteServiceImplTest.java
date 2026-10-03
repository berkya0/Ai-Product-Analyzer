package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.dto.SiteCreateRequest;
import com.berkaykomur.backend.dto.SiteResponse;
import com.berkaykomur.backend.exception.site.SiteNotFoundException;
import com.berkaykomur.backend.mapper.SiteMapper;
import com.berkaykomur.backend.model.Site;
import com.berkaykomur.backend.model.UserEntity;
import com.berkaykomur.backend.repository.SiteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SiteServiceImplTest {

    @Mock
    private SiteRepository siteRepository;

    @Mock
    private SiteMapper siteMapper;

    @InjectMocks
    private SiteServiceImpl siteService;

    private UserEntity mockUser;
    private SiteCreateRequest mockRequest;
    private Site mockSite;
    private SiteResponse mockResponse;

    @BeforeEach
    void setUp() {
        // Testlerde ortak kullanılacak sahte nesneleri ilklendiriyoruz
        mockUser = UserEntity.builder()
                .id(1L)
                .username("berkay")
                .build();

        mockRequest = new SiteCreateRequest("Teknoloji Blogu", "https://berkay.com", "admin", "app-pass-123");

        mockSite = Site.builder()
                .id(100L)
                .siteName("Teknoloji Blogu")
                .siteUrl("https://berkay.com")
                .username("admin")
                .appPassword("app-pass-123")
                .user(mockUser)
                .build();

        mockResponse = new SiteResponse(100L, "Teknoloji Blogu", "https://berkay.com", "admin");
    }

    // ==========================================
    // 1. ADD SITE TESTLERİ
    // ==========================================

    @Test
    @DisplayName("addSite: İsteğe uygun Site oluşturulup kullanıcıya bağlanmalı ve veritabanına kaydedilmeli")
    void addSite_WhenValidRequestAndUserProvided_ShouldMapAndSave() {
        // ARRANGE
        Site unmappedSite = new Site(); // Mapper'dan dönecek boş/temel site nesnesi
        when(siteMapper.createSiteRequest(mockRequest)).thenReturn(unmappedSite);

        // ACT
        siteService.addSite(mockRequest, mockUser);

        // ASSERT
        assertEquals(mockUser, unmappedSite.getUser()); // Kullanıcının nesneye bağlandığını doğruluyoruz
        verify(siteMapper, times(1)).createSiteRequest(mockRequest);
        verify(siteRepository, times(1)).save(unmappedSite);
    }

    // ==========================================
    // 2. GET ACTIVE SITES TESTLERİ
    // ==========================================

    @Test
    @DisplayName("getActiveSites: Kullanıcının aktif sitelerini çekip DTO listesine dönüştürmeli")
    void getActiveSites_WhenUserIdProvided_ShouldReturnSiteResponseList() {
        // ARRANGE
        Long userId=mockUser.getId();
        List<Site> siteList = List.of(mockSite);
        List<SiteResponse> expectedResponses = List.of(mockResponse);

        when(siteRepository.findAllByUser_Id(userId)).thenReturn(siteList);
        when(siteMapper.siteToSiteResponseList(siteList)).thenReturn(expectedResponses);

        // ACT
        List<SiteResponse> actualResponses = siteService.getActiveSites(userId);

        // ASSERT
        assertNotNull(actualResponses);
        assertEquals(expectedResponses, actualResponses);

        verify(siteRepository, times(1)).findAllByUser_Id(userId);
        verify(siteMapper, times(1)).siteToSiteResponseList(siteList);
    }

    // ==========================================
    // 3. DELETE SITE TESTLERİ
    // ==========================================

    @Test
    @DisplayName("deleteSite [Mutlu Yol]: Site veritabanında varsa başarıyla silinmeli")
    void deleteSite_WhenSiteExists_ShouldDeleteById() {
        // ARRANGE
        Long siteId = 100L;
        Long userId = 1L;

        when(siteRepository.existsByIdAndUser_Id(siteId, userId)).thenReturn(true);

        // ACT
        siteService.deleteSite(siteId, userId);

        // ASSERT
        verify(siteRepository, times(1)).existsByIdAndUser_Id(siteId, userId);
        verify(siteRepository, times(1)).deleteById(siteId);
    }

    @Test
    @DisplayName("deleteSite [Hata Yolu]: Site bulunamadığında SiteNotFoundException fırlatmalı ve silme metodunu çağırmamalı")
    void deleteSite_WhenSiteDoesNotExist_ShouldThrowExceptionAndNotDelete() {
        // ARRANGE
        Long siteId = 100L;
        Long userId = 1L;

        when(siteRepository.existsByIdAndUser_Id(siteId, userId)).thenReturn(false);

        // ACT & ASSERT
        assertThrows(SiteNotFoundException.class, () -> siteService.deleteSite(siteId, userId));

        // Yan Etki Kontrolü: Hata fırladığı için deleteById metodu HİÇ ÇAĞRILMAMIŞ olmalı!
        verify(siteRepository, times(1)).existsByIdAndUser_Id(siteId, userId);
        verify(siteRepository, never()).deleteById(anyLong());
    }
}