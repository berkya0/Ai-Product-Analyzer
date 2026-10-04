package com.berkaykomur.backend.exception.analysis;

import com.berkaykomur.backend.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AiAnalysisFailedException extends BaseException {
    public AiAnalysisFailedException(String message, Throwable cause) {

        super(message, HttpStatus.INTERNAL_SERVER_ERROR,cause);
    }
}
