package com.berkaykomur.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record ProductAnalyzeRequest(
        @NotBlank(message = "Ürün linki girilmedi")
        String productUrl
) {

}
