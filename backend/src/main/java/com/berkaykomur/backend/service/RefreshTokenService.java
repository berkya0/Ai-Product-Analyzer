package com.berkaykomur.backend.service;

import com.berkaykomur.backend.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(Long userId);
    RefreshToken verifyExpiration(RefreshToken token);
    void deleteByToken(String refreshToken);
    Optional<RefreshToken> findByToken(String token);
}
