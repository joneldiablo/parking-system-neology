package com.neology.parking.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "test-secret-para-jwt-2026";

    private CryptoIdService crypto;
    private JwtService jwt;

    @BeforeEach
    void setUp() {
        crypto = new CryptoIdService(SECRET);
        jwt = new JwtService(crypto, SECRET, 3);
    }

    @Test
    void emiteTokenYRecuperaElId() {
        Long id = 123L;
        String token = jwt.createToken(id);

        assertThat(token).isNotBlank();
        assertThat(jwt.parseUserId(token)).isEqualTo(id);
    }

    @Test
    void elIdEstaCifradoDentroDelToken() {
        String token = jwt.createToken(123L);
        String[] partes = token.split("\\.");

        // El payload (2ª parte) NO contiene el id en claro
        assertThat(partes[1]).isNotEqualTo("123");
        assertThat(new String(java.util.Base64.getUrlDecoder().decode(partes[1])))
                .doesNotContain("123");
    }

    @Test
    void tokenFirmadoConOtraClaveEsRechazado() {
        JwtService otro = new JwtService(crypto, "otra-clave-para-firma", 3);
        String token = otro.createToken(5L);

        assertThatThrownBy(() -> jwt.parseUserId(token))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void tokenExpiradoEsRechazado() {
        JwtService expirado = new JwtService(crypto, SECRET, -1);
        String token = expirado.createToken(5L);

        assertThatThrownBy(() -> expirado.parseUserId(token))
                .isInstanceOf(ExpiredJwtException.class);
    }
}