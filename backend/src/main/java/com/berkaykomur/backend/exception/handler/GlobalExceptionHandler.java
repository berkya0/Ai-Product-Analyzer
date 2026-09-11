package com.berkaykomur.backend.exception.handler;

import com.berkaykomur.backend.exception.ApiError;
import com.berkaykomur.backend.exception.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiError> handleException(BaseException e, HttpServletRequest request) {
        log.error("İş kuralı hatası oluştu: {} | Path: {}", e.getMessage(), request.getRequestURI(), e);
        ApiError apiError =ApiError.builder()
                .error(e.getStatus().getReasonPhrase())
                .status(e.getStatus().value())
                .path(request.getRequestURI())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(e.getStatus()).body(apiError);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {

        Map<String, String> validationErrors = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            validationErrors.put(error.getField(), error.getDefaultMessage());
        }
        log.warn("Validasyon hatası oluştu. Path: {}", request.getRequestURI());

        ApiError apiError = ApiError.builder()
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .message("Doğrulama hataları mevcut.")
                .validationErrors(validationErrors)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e,
                                                    HttpServletRequest request) {
        log.error("Beklenmeyen sistem hatası! Path: {}", request.getRequestURI(), e);
        ApiError apiError = ApiError.builder()
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getRequestURI())
                .message("Unexpected error occurred")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiError);
    }
}
