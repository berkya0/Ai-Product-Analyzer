package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.exception.refreshToken.RefreshTokenNotFoundException;
import com.berkaykomur.backend.exception.user.UsernameNotFoundException;
import com.berkaykomur.backend.model.RefreshToken;
import com.berkaykomur.backend.model.UserEntity;
import com.berkaykomur.backend.repository.RefreshTokenRepository;
import com.berkaykomur.backend.repository.UserRepository;
import com.berkaykomur.backend.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private UserEntity  mockUser;
    private RefreshToken mockRefreshToken;
    @BeforeEach
    void setUp() {
        mockUser = new UserEntity();
        mockUser.setId(1L);
        mockRefreshToken = new RefreshToken();
        mockRefreshToken.setRefreshToken(UUID.randomUUID().toString());

        mockRefreshToken.setUser(mockUser);

        // Spring context olmadığı için @Value("${...}") ile dolan expiration alanını elle veriyoruz (3600 saniye)
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpiration", 3600L);
    }

    @Test
    @DisplayName("createRefreshToken: Kullanıcı bulunduğunda başarıyla RefreshToken oluşturup kaydetmeli")
    void createRefreshToken_WhenUserExists_ShouldCreateAndSaveToken() {
        // ARRANGE
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // save metodu çağrıldığında içine gönderilen RefreshToken nesnesini aynen geri dönsün
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        RefreshToken result = refreshTokenService.createRefreshToken(userId);

        // ASSERT
        assertNotNull(result);
        assertNotNull(result.getRefreshToken());  // UUID oluşmuş mu?
        assertNotNull(result.getExpiredDate());   // Son kullanma tarihi atanmış mı?
        assertEquals(mockUser, result.getUser()); // Kullanıcı nesneye bağlanmış mı?

        verify(userRepository, times(1)).findById(userId);
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("createRefreshToken: Kullanıcı bulunamadığında UsernameNotFoundException fırlatmalı")
    void createRefreshToken_WhenUserDoesNotExist_ShouldThrowException() {
        // ARRANGE
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        UsernameNotFoundException exception= assertThrows(
                UsernameNotFoundException.class,
                () -> refreshTokenService.createRefreshToken(userId)
        );

        verify(userRepository, times(1)).findById(userId);
        assertEquals("Kullanıcı bulunamadı: "+userId, exception.getMessage());
        verifyNoInteractions(refreshTokenRepository);
    }
    @Test
    @DisplayName("findByToken: Token bulunduğunda RefreshToken dönmeli")
    void findByToken_WhenTokenExists_ShouldReturnRefreshToken() {
        // ARRANGE
        String token = "sample-token-123";
        RefreshToken mockRefreshToken = new RefreshToken();
        when(refreshTokenRepository.findByRefreshToken(token)).thenReturn(Optional.of(mockRefreshToken));

        // ACT
        Optional<RefreshToken> result = refreshTokenService.findByToken(token);

        // ASSERT
        assertTrue(result.isPresent()); // Kutunun içinin dolu olduğunu kontrol et
        assertEquals(mockRefreshToken, result.get()); // İçindeki verinin doğru olduğunu kontrol et
        verify(refreshTokenRepository, times(1)).findByRefreshToken(token);
    }

    @Test
    @DisplayName("verifyExpiration: Token süresi dolmamışsa aynı token'ı geri dönmeli")
    void verifyExpiration_WhenTokenNotExpired_ShouldReturnToken() {
        // ARRANGE - Gelecekte bir son kullanma tarihi veriyoruz
        mockRefreshToken.setExpiredDate(LocalDateTime.now().plusHours(1));

        // ACT
        RefreshToken result = refreshTokenService.verifyExpiration(mockRefreshToken);

        // ASSERT
        assertNotNull(result);
        assertEquals(mockRefreshToken, result);
        // Süresi dolmadığı için silme işlemi yapılmamalı!
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    @DisplayName("verifyExpiration: Token süresi dolmuşsa silinmeli ve RefreshTokenNotFoundException fırlatmalı")
    void verifyExpiration_WhenTokenExpired_ShouldDeleteTokenAndThrowException() {
        // ARRANGE - Geçmişte kalan bir son kullanma tarihi veriyoruz
        RefreshToken expiredToken = new RefreshToken();
        expiredToken.setExpiredDate(LocalDateTime.now().minusHours(1));

        // ACT & ASSERT
        RefreshTokenNotFoundException exception = assertThrows(
                RefreshTokenNotFoundException.class,
                () -> refreshTokenService.verifyExpiration(expiredToken)
        );

        assertEquals("Refresh token süresi doldu. Lütfen tekrar giriş yapın.", exception.getMessage());
        // Veritabanından silindiğini doğruluyoruz
        verify(refreshTokenRepository, times(1)).delete(expiredToken);
    }

    @Test
    @DisplayName("Geçerli token ve userId verildiğinde repository silme metodu çağrılmalı")
    void deleteByToken_WhenCalled_ShouldInvokeRepositoryDelete() {

        refreshTokenService.deleteByToken(mockRefreshToken.getRefreshToken(), mockUser.getId());
        verify(refreshTokenRepository, times(1))
                .deleteByTokenAndUserId(mockRefreshToken.getRefreshToken(), mockUser.getId());
    }
}