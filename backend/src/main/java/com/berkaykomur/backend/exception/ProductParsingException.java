package com.berkaykomur.backend.exception;

import org.springframework.http.HttpStatus;

public class ProductParsingException extends BaseException{

    public ProductParsingException(String message) {
        super(message, HttpStatus.BAD_GATEWAY);
    }
}
