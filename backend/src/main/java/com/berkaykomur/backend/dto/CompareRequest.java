package com.berkaykomur.backend.dto;

import java.util.List;

public record CompareRequest(
        List<Long> productIds
) {}
