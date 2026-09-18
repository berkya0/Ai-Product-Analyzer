package com.berkaykomur.backend.exception.site;

import com.berkaykomur.backend.exception.BaseException;
import org.springframework.http.HttpStatus;

public class EncryptionException extends BaseException {
    public EncryptionException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public EncryptionException(String message, Throwable cause) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}
