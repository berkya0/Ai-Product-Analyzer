package com.berkaykomur.backend.exception;

import org.springframework.http.HttpStatus;

public class ScrapingConnectionException extends BaseException {
    public ScrapingConnectionException(String errorMessage) {
        super(errorMessage, HttpStatus.SERVICE_UNAVAILABLE);

    }
}
