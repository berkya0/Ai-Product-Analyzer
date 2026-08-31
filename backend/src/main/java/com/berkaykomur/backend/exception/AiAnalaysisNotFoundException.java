package com.berkaykomur.backend.exception;

import org.springframework.http.HttpStatus;

public class AiAnalaysisNotFoundException extends BaseException {
    public AiAnalaysisNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
    public AiAnalaysisNotFoundException(String message,Throwable cause) {
        super(message, HttpStatus.NOT_FOUND,cause);
    }
}
