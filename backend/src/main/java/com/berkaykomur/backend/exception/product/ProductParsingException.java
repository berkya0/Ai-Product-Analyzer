package com.berkaykomur.backend.exception.product;

import com.berkaykomur.backend.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ProductParsingException extends BaseException {

    public ProductParsingException(String message) {
        super(message, HttpStatus.BAD_GATEWAY);
    }
    public ProductParsingException(String message,Throwable cause) {
        super(message, HttpStatus.BAD_GATEWAY,cause);
    }
}
