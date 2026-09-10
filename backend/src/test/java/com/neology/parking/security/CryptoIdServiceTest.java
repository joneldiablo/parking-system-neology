package com.neology.parking.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CryptoIdServiceTest {

    private CryptoIdService crypto;

    @BeforeEach
    void setUp() {
        crypto = new CryptoIdService("test-secret");
    }

    @Test
    void cifraYDescifraElId() {
        Long id = 42L;
        String cifrado = crypto.encrypt(id);

        assertThat(cifrado).isNotEqualTo("42");
        assertThat(crypto.decrypt(cifrado)).isEqualTo(id);
    }

    @Test
    void mismoIdGeneraCifradosDistintosGCM() {
        String a = crypto.encrypt(7L);
        String b = crypto.encrypt(7L);

        assertThat(a).isNotEqualTo(b);
        assertThat(crypto.decrypt(a)).isEqualTo(crypto.decrypt(b));
    }

    @Test
    void datoConOtraClaveNoSeDescifra() {
        String cifrado = new CryptoIdService("otra-clave").encrypt(1L);

        assertThatThrownBy(() -> crypto.decrypt(cifrado))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void payloadInvalidoLanzaError() {
        assertThatThrownBy(() -> crypto.decrypt("no-es-un-cifrado-valido"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}