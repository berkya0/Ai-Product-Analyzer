package com.berkaykomur.backend.exception.analysis;

import com.berkaykomur.backend.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ScrapingConnectionException extends BaseException {
    public ScrapingConnectionException(String errorMessage) {
        super(errorMessage, HttpStatus.SERVICE_UNAVAILABLE);
    }

    public ScrapingConnectionException(String errorMessage, Throwable cause) {
        super(errorMessage, HttpStatus.SERVICE_UNAVAILABLE,cause);
    }
}
