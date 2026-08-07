package com.berkaykomur.backend.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
public class ApiError {

    private final int status;
    private final String message;
    private final String error;
    @Builder.Default
    private final Instant timestamp = Instant.now();
    private final String path;
    private final Map<String, String> validationErrors;

}
