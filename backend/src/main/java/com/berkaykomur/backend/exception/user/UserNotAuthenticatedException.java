package com.berkaykomur.backend.exception.user;

import com.berkaykomur.backend.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserNotAuthenticatedException extends BaseException {
    public UserNotAuthenticatedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
