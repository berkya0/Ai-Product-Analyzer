package com.berkaykomur.backend.exception;

import org.springframework.http.HttpStatus;

public class InvalidProductUrlException extends BaseException{
    public InvalidProductUrlException(String message) {
        super(message,HttpStatus.BAD_REQUEST);
    }
}
