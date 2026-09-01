package com.berkaykomur.backend.util;

import com.berkaykomur.backend.exception.EncryptionException;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Converter
public class PasswordEncryptionConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES";
    @Value("${app.encryption.secret}")
    private String secretKey;

    @Override
    public String convertToDatabaseColumn(String rawData) {
        if (rawData == null) return null;
        try {
            Key key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(rawData.getBytes()));
        } catch (Exception e) {
            throw new EncryptionException("Veritabanına yazarken şifreleme işlemi başarısız oldu!", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String encryptedData) {
        if (encryptedData == null) return null;
        try {
            Key key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
        } catch (Exception e) {
            throw new EncryptionException("Veritabanından okurken şifre çözme işlemi başarısız oldu!", e);
        }
    }
}