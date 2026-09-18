package com.berkaykomur.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Kullanıcı adı boş olamaz")
        @Size(min=3,message = "Kullanıcı adı en az üç karakterden oluşmalı")
        String username,

        @Size(min = 8,message = "Şifre en az 8 karakter içermeli")
        @NotBlank(message = "Lütfen şifrenizi seçin")
        String password,

        @NotBlank(message = "E-mail adresi boş olamaz")
        @Email(message = "Geçerli bir mail adresi giriniz")
        String email
) {
}
