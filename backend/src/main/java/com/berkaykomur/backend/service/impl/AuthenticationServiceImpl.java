package com.berkaykomur.backend.service.impl;


import com.berkaykomur.backend.dto.AuthResponse;
import com.berkaykomur.backend.dto.LoginRequest;
import com.berkaykomur.backend.dto.RefreshTokenRequest;
import com.berkaykomur.backend.dto.RegisterRequest;
import com.berkaykomur.backend.exception.user.EmailAlreadyExistsException;
import com.berkaykomur.backend.exception.user.UserAlreadyExistsException;
import com.berkaykomur.backend.exception.user.UsernameNotFoundException;

import com.berkaykomur.backend.jwt.CustomUserDetails;

import com.berkaykomur.backend.jwt.JwtService;
import com.berkaykomur.backend.model.RefreshToken;
import com.berkaykomur.backend.model.UserEntity;
import com.berkaykomur.backend.repository.UserRepository;
import com.berkaykomur.backend.service.AuthenticationService;
import com.berkaykomur.backend.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Yeni kullanıcı kayıt işlemi başlatıldı: {}", request.username());
        if(userRepository.existsByUsername(request.username())){
            throw new UserAlreadyExistsException("Kullanıcı adı seçilmiş: "+request.username());

        }
        if(userRepository.existsByEmail(request.email())){
            throw new EmailAlreadyExistsException("Email sisteme kayıtlı: "+request.email());
        }
        UserEntity user = UserEntity.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .build();

        UserEntity savedUser = userRepository.save(user);
        log.info("Kullanıcı başarıyla kaydedildi: {}", request.username());

        String accessToken = jwtService.generateToken(new HashMap<>(),  new CustomUserDetails(savedUser));
        RefreshToken refreshToken=refreshTokenService.createRefreshToken(savedUser.getId());
        return new AuthResponse(savedUser.getId(),request.username(),accessToken,refreshToken.getRefreshToken());
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Giriş denemesi: {}", request.username());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserEntity user = userRepository.findByUsername(request.username())
                .orElseThrow(()->new UsernameNotFoundException("Kullanıcı adı bulunamadı: "+request.username()));
        log.info("Giriş başarılı: {}", request.username());

        String accessToken = jwtService.generateToken(new HashMap<>(), new CustomUserDetails(user));
        RefreshToken refreshToken=refreshTokenService.createRefreshToken(user.getId());
        return new AuthResponse(user.getId(),request.username(),accessToken,refreshToken.getRefreshToken());
    }

    @Override
    @Transactional
    public AuthResponse refreshAccessToken(RefreshTokenRequest requestRefreshToken) {
        return refreshTokenService.findByToken(requestRefreshToken.refreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtService.generateToken(new HashMap<>(), new CustomUserDetails(user));
                    refreshTokenService.deleteByToken(requestRefreshToken.refreshToken());
                    RefreshToken refreshToken=refreshTokenService.createRefreshToken(user.getId());
                    return new AuthResponse(user.getId(), user.getUsername(), accessToken, refreshToken.getRefreshToken());
                })
                .orElseThrow(() -> new RuntimeException("Refresh token bulunamadı veya geçersiz!"));
    }


}