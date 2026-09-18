package com.berkaykomur.backend.exception.analysis;

import com.berkaykomur.backend.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidProductUrlException extends BaseException {
    public InvalidProductUrlException(String message) {
        super(message,HttpStatus.BAD_REQUEST);
    }
}
