package com.berkaykomur.backend.exception.user;

import com.berkaykomur.backend.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UsernameNotFoundException extends BaseException {
    public UsernameNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
