package com.berkaykomur.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiError> handleException(BaseException e, HttpServletRequest request) {
        ApiError apiError =ApiError.builder()
                .error(e.getStatus().getReasonPhrase())
                .status(e.getStatus().value())
                .path(request.getRequestURI())
                .message(e.getMessage())
                .build();
        e.printStackTrace();
        return ResponseEntity.status(e.getStatus()).body(apiError);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e,
                                                    HttpServletRequest request) {
        ApiError apiError = ApiError.builder()
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getRequestURI())
                .message("Unexpected error occurred")
                .build();
        e.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiError);
    }
}
