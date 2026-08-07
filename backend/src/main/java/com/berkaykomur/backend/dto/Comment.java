package com.berkaykomur.backend.dto;

public record Comment(
        Integer rate,
        String text,
        Integer likesCount

) {}
