package com.berkaykomur.backend.exception.analysis;

import com.berkaykomur.backend.exception.BaseException;
import org.springframework.http.HttpStatus;

public class NoCommentException extends BaseException {
    public NoCommentException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

}
