package com.berkaykomur.backend.exception;

import org.springframework.http.HttpStatus;

public class SiteNotFoundException extends BaseException {
    public SiteNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
