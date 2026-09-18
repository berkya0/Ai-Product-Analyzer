package com.berkaykomur.backend.exception.analysis;

import com.berkaykomur.backend.exception.BaseException;
import org.springframework.http.HttpStatus;

public class JsonLdNotFoundException extends BaseException {
    public JsonLdNotFoundException(String message) {
        super(message, HttpStatus.BAD_GATEWAY);
    }
}
