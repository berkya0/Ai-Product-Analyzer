package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.dto.AuthResponse;
import com.berkaykomur.backend.dto.LoginRequest;
import com.berkaykomur.backend.dto.RefreshTokenRequest;
import com.berkaykomur.backend.dto.RegisterRequest;
import com.berkaykomur.backend.exception.refreshToken.RefreshTokenNotFoundException;
import com.berkaykomur.backend.exception.user.EmailAlreadyExistsException;
import com.berkaykomur.backend.exception.user.UserAlreadyExistsException;
import com.berkaykomur.backend.exception.user.UsernameNotFoundException;
import com.berkaykomur.backend.jwt.JwtService;
import com.berkaykomur.backend.model.RefreshToken;
import com.berkaykomur.backend.model.UserEntity;
import com.berkaykomur.backend.repository.UserRepository;
import com.berkaykomur.backend.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private UserEntity mockUser;
    private RefreshToken mockRefreshToken;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private RefreshTokenRequest refreshTokenRequest;

    private final Long userId = 1L;
    private final String username = "berkay";
    private final String email = "berkay@example.com";
    private final String rawPassword = "password123";
    private final String encodedPassword = "encoded_password_123";
    private final String accessToken = "mocked-access-token";
    private final String refreshTokenStr = "mocked-refresh-token";

    @BeforeEach
    void setUp() {
        mockUser = UserEntity.builder()
                .id(userId)
                .fullName("Berkay Kömür")
                .username(username)
                .email(email)
                .password(encodedPassword)
                .build();

        mockRefreshToken = new RefreshToken();
        mockRefreshToken.setId(10L);
        mockRefreshToken.setRefreshToken(refreshTokenStr);
        mockRefreshToken.setUser(mockUser);

        registerRequest = new RegisterRequest("berkay kömür",username,rawPassword,email);
        loginRequest = new LoginRequest(username, rawPassword);
        refreshTokenRequest = new RefreshTokenRequest(refreshTokenStr);
    }


    @Test
    @DisplayName("register - Başarılı kayıt: Kullanıcı kaydedilmeli, tokenlar üretilmeli ve AuthResponse dönmeli")
    void register_WhenSuccessful_ShouldRegisterUserAndReturnAuthResponse() {
        // ARRANGE
        when(userRepository.existsByUsername(registerRequest.username())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn(encodedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUser);
        when(jwtService.generateToken(anyMap(), any())).thenReturn(accessToken);
        when(refreshTokenService.createRefreshToken(userId)).thenReturn(mockRefreshToken);

        // ACT
        AuthResponse response = authenticationService.register(registerRequest);

        // ASSERT
        assertNotNull(response);
        assertEquals(userId, response.id());
        assertEquals(username, response.username());
        assertEquals(accessToken, response.accessToken());
        assertEquals(refreshTokenStr, response.refreshToken());

        verify(userRepository, times(1)).existsByUsername(registerRequest.username());
        verify(userRepository, times(1)).existsByEmail(registerRequest.email());
        verify(passwordEncoder, times(1)).encode(registerRequest.password());
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(jwtService, times(1)).generateToken(anyMap(), any());
        verify(refreshTokenService, times(1)).createRefreshToken(userId);
    }

    @Test
    @DisplayName("register - Kullanıcı adı zaten varsa: UserAlreadyExistsException fırlatılmalı")
    void register_WhenUsernameAlreadyExists_ShouldThrowUserAlreadyExistsException() {
        // ARRANGE
        when(userRepository.existsByUsername(registerRequest.username())).thenReturn(true);

        // ACT & ASSERT
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> authenticationService.register(registerRequest)
        );

        assertTrue(exception.getMessage().contains("Kullanıcı adı seçilmiş: " + username));
        verify(userRepository, times(1)).existsByUsername(username);
        verifyNoMoreInteractions(userRepository);

        verifyNoInteractions(jwtService, refreshTokenService, passwordEncoder);
    }

    @Test
    @DisplayName("register - Email zaten varsa: EmailAlreadyExistsException fırlatılmalı")
    void register_WhenEmailAlreadyExists_ShouldThrowEmailAlreadyExistsException() {
        // ARRANGE
        when(userRepository.existsByUsername(registerRequest.username())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(true);

        // ACT & ASSERT
        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> authenticationService.register(registerRequest)
        );

        assertTrue(exception.getMessage().contains("Email sisteme kayıtlı: "+registerRequest.email()));
        verify(userRepository, times(1)).existsByUsername(username);
        verify(userRepository, times(1)).existsByEmail(email);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(jwtService, refreshTokenService, passwordEncoder);
    }

    @Test
    @DisplayName("login - Başarılı giriş: Doğrulama yapılmalı, tokenlar üretilmeli ve AuthResponse dönmeli")
    void login_WhenSuccessful_ShouldAuthenticateAndReturnAuthResponse() {
        // ARRANGE
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByUsername(loginRequest.username())).thenReturn(Optional.of(mockUser));
        when(jwtService.generateToken(anyMap(), any())).thenReturn(accessToken);
        when(refreshTokenService.createRefreshToken(userId)).thenReturn(mockRefreshToken);

        // ACT
        AuthResponse response = authenticationService.login(loginRequest);

        // ASSERT
        assertNotNull(response);
        assertEquals(userId, response.id());
        assertEquals(username, response.username());
        assertEquals(accessToken, response.accessToken());
        assertEquals(refreshTokenStr, response.refreshToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByUsername(username);
        verify(jwtService, times(1)).generateToken(anyMap(), any());
        verify(refreshTokenService, times(1)).createRefreshToken(userId);
    }

    @Test
    @DisplayName("login - Kullanıcı adı veritabanında bulunamazsa: UsernameNotFoundException fırlatılmalı")
    void login_WhenUserNotFound_ShouldThrowUsernameNotFoundException() {
        // ARRANGE
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByUsername(loginRequest.username())).thenReturn(Optional.empty());

        // ACT & ASSERT
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> authenticationService.login(loginRequest)
        );

        assertTrue(exception.getMessage().contains("Kullanıcı adı bulunamadı: " + username));
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByUsername(username);
        verifyNoInteractions(jwtService,refreshTokenService);
    }

    @Test
    @DisplayName("login - Hatalı şifre girdiğinde: AuthenticationManager exception fırlatmalı")
    void login_WhenBadCredentials_ShouldThrowBadCredentialsException() {
        // ARRANGE
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Hatalı kullanıcı adı veya şifre"));

        // ACT & ASSERT
        assertThrows(
                BadCredentialsException.class,
                () -> authenticationService.login(loginRequest)
        );

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(userRepository);
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("refreshAccessToken - Geçerli token: Eski token silinmeli, yeni tokenlar üretilmeli")
    void refreshAccessToken_WhenSuccessful_ShouldReturnNewAuthResponse() {
        // ARRANGE
        when(refreshTokenService.findByToken(refreshTokenRequest.refreshToken())).thenReturn(Optional.of(mockRefreshToken));
        when(refreshTokenService.verifyExpiration(mockRefreshToken)).thenReturn(mockRefreshToken);
        when(jwtService.generateToken(anyMap(), any())).thenReturn("new-access-token");
        when(refreshTokenService.createRefreshToken(userId)).thenReturn(mockRefreshToken);

        // ACT
        AuthResponse response = authenticationService.refreshAccessToken(refreshTokenRequest);

        // ASSERT
        assertNotNull(response);
        assertEquals(userId, response.id());
        assertEquals("new-access-token", response.accessToken());

        verify(refreshTokenService, times(1)).findByToken(refreshTokenStr);
        verify(refreshTokenService, times(1)).verifyExpiration(mockRefreshToken);
        verify(refreshTokenService, times(1)).deleteByToken(refreshTokenStr, userId);
        verify(jwtService, times(1)).generateToken(anyMap(), any());
        verify(refreshTokenService, times(1)).createRefreshToken(userId);
    }

    @Test
    @DisplayName("refreshAccessToken - Token bulunamazsa: RefreshTokenNotFoundException fırlatılmalı")
    void refreshAccessToken_WhenTokenNotFound_ShouldThrowRefreshTokenNotFoundException() {
        // ARRANGE
        when(refreshTokenService.findByToken(refreshTokenRequest.refreshToken())).thenReturn(Optional.empty());

        // ACT & ASSERT
        RefreshTokenNotFoundException exception = assertThrows(
                RefreshTokenNotFoundException.class,
                () -> authenticationService.refreshAccessToken(refreshTokenRequest)
        );

        assertEquals("Refresh token bulunamadı veya geçersiz!", exception.getMessage());
        verify(refreshTokenService, times(1)).findByToken(refreshTokenStr);
        verifyNoMoreInteractions(refreshTokenService);
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("logout - Geçerli refresh token ile: Token veritabanından silinmeli")
    void logout_WhenValidRequest_ShouldDeleteRefreshToken() {
        // ACT
        authenticationService.logout(refreshTokenRequest, userId);

        // ASSERT
        verify(refreshTokenService, times(1)).deleteByToken(refreshTokenRequest.refreshToken(), userId);
    }

    @Test
    @DisplayName("logout - Request nesnesi null olduğunda silme işlemi yapılmamalı")
    void logout_WhenRequestIsNull_ShouldNotCallDelete() {
        // ACT
        authenticationService.logout(null, userId);

        // ASSERT
        verifyNoInteractions(refreshTokenService);
    }

    @Test
    @DisplayName("logout - Token boş string olduğunda silme işlemi yapılmamalı")
    void logout_WhenRefreshTokenIsEmpty_ShouldNotCallDelete() {
        // ACT
        authenticationService.logout(new RefreshTokenRequest(""), userId);

        // ASSERT
        verifyNoInteractions(refreshTokenService);
    }

    @Test
    @DisplayName("logout - Request içindeki token null olduğunda silme işlemi yapılmamalı")
    void logout_WhenRefreshTokenIsNull_ShouldNotCallDelete() {
        // ACT
        authenticationService.logout(new RefreshTokenRequest(null), userId);

        // ASSERT
        verifyNoInteractions(refreshTokenService);
    }
}