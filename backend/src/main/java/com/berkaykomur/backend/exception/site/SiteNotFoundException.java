package com.berkaykomur.backend.exception.site;

import com.berkaykomur.backend.exception.BaseException;
import org.springframework.http.HttpStatus;

public class SiteNotFoundException extends BaseException {
    public SiteNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
