package com.berkaykomur.backend.exception.refreshToken;

import com.berkaykomur.backend.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RefreshTokenNotFoundException extends BaseException {
    public RefreshTokenNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
