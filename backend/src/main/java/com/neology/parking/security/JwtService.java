package com.neology.parking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;

/**
 * Emite y valida el JWT de sesión.
 * El único claim es el id de usuario, que viaja CIFRADO (ver {@link CryptoIdService}):
 * incluso rompiendo el JWT la identidad no es legible sin la clave de cifrado.
 */
@Service
public class JwtService {

    private final CryptoIdService cryptoId;
    private final SecretKey signingKey;
    private final long expirationMillis;

    public JwtService(CryptoIdService cryptoId,
                      @Value("${jwt.secret}") String jwtSecret,
                      @Value("${jwt.expiration-hours}") long expirationHours) {
        this.cryptoId = cryptoId;
        this.signingKey = new SecretKeySpec(sha256(jwtSecret), "HmacSHA256");
        this.expirationMillis = expirationHours * 3600_000L;
    }

    public String createToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(cryptoId.encrypt(userId))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMillis))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Valida firma y expiración, descifra el id y lo devuelve.
     *
     * @throws io.jsonwebtoken.JwtException si el token es inválido o expiró
     */
    public Long parseUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return cryptoId.decrypt(claims.getSubject());
    }

    private static byte[] sha256(String value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo derivar la clave de firma", e);
        }
    }
}