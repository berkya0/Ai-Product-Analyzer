package com.berkaykomur.backend.exception.product;

import com.berkaykomur.backend.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CategoryMismatchException extends BaseException {
    public CategoryMismatchException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
