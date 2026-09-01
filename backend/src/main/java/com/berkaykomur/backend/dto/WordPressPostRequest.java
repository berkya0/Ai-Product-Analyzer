package com.berkaykomur.backend.dto;

public record WordPressPostRequest(
        
        String title,
        String content,
        String status
) {}