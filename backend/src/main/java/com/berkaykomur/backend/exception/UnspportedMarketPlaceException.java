package com.berkaykomur.backend.exception;

import org.springframework.http.HttpStatus;

public class UnspportedMarketPlaceException extends BaseException{
    public UnspportedMarketPlaceException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
