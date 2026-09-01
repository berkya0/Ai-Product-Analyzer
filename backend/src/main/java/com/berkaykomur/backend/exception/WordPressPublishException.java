package com.berkaykomur.backend.exception;

import org.springframework.http.HttpStatus;

public class WordPressPublishException extends BaseException {
    public WordPressPublishException(String message) {
        super(message, HttpStatus.BAD_GATEWAY);
    }
    public WordPressPublishException(String message,Throwable cause) {
        super(message, HttpStatus.BAD_GATEWAY,cause);
    }
}
