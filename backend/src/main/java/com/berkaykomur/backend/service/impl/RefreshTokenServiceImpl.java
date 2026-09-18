package com.berkaykomur.backend.service.impl;

import com.berkaykomur.backend.exception.refreshToken.RefreshTokenNotFoundException;
import com.berkaykomur.backend.exception.user.UsernameNotFoundException;
import com.berkaykomur.backend.model.RefreshToken;
import com.berkaykomur.backend.model.UserEntity;
import com.berkaykomur.backend.repository.RefreshTokenRepository;
import com.berkaykomur.backend.repository.UserRepository;
import com.berkaykomur.backend.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(()->new UsernameNotFoundException("Kullanıcı bulunamadı: "+userId));

        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(UUID.randomUUID().toString())
                .expiredDate(LocalDateTime.now().plusSeconds(refreshTokenExpiration))
                .user(user)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByRefreshToken(token);
    }

    @Transactional
    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiredDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenNotFoundException("Refresh token süresi doldu. Lütfen tekrar giriş yapın.");
        }
        return token;
    }

    @Transactional
    @Override
    public void deleteByToken(String refreshToken) {
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }


}
