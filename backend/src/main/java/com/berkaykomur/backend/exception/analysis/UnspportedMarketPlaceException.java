package com.berkaykomur.backend.exception.analysis;

import com.berkaykomur.backend.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UnspportedMarketPlaceException extends BaseException {
    public UnspportedMarketPlaceException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
