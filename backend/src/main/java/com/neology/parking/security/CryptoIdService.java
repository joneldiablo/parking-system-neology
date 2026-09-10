package com.neology.parking.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Cifra/descifra el id de usuario que viaja DENTRO del JWT.
 * Doble capa: el JWT va firmado (HMAC) y el único claim (el id) va además cifrado (AES/GCM).
 */
@Component
public class CryptoIdService {

    private static final int GCM_TAG_BITS = 128;
    private static final int IV_BYTES = 12;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final SecretKeySpec key;

    public CryptoIdService(@Value("${jwt.secret}") String jwtSecret) {
        byte[] rawKey = sha256(jwtSecret);
        this.key = new SecretKeySpec(rawKey, "AES");
    }

    public String encrypt(Long id) {
        try {
            byte[] iv = new byte[IV_BYTES];
            RANDOM.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] ciphertext = cipher.doFinal(String.valueOf(id).getBytes(StandardCharsets.UTF_8));
            return base64(iv) + "." + base64(ciphertext);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo cifrar el id de usuario", e);
        }
    }

    public Long decrypt(String payload) {
        try {
            String[] parts = payload.split("\\.");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Payload cifrado inválido");
            }
            byte[] iv = Base64.getUrlDecoder().decode(parts[0]);
            byte[] ciphertext = Base64.getUrlDecoder().decode(parts[1]);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
            return Long.valueOf(new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalArgumentException("No se pudo descifrar el id de usuario", e);
        }
    }

    private static String base64(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    private static byte[] sha256(String value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo derivar la clave", e);
        }
    }
}