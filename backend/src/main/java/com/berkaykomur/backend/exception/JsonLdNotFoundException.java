package com.berkaykomur.backend.exception;

import org.springframework.http.HttpStatus;

public class JsonLdNotFoundException extends BaseException{
    public JsonLdNotFoundException(String message) {
        super(message, HttpStatus.BAD_GATEWAY);
    }
}
