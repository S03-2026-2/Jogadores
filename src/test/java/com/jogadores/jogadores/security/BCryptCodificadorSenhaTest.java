package com.jogadores.jogadores.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BCryptCodificadorSenhaTest {

    private final BCryptCodificadorSenha codificador = new BCryptCodificadorSenha();

    @Test
    void confereSenhaCorretaENaoGuardaTextoPuro() {
        String hash = codificador.codificar("pikachu123");

        assertNotEquals("pikachu123", hash);
        assertTrue(codificador.confere("pikachu123", hash));
        assertFalse(codificador.confere("charizard456", hash));
    }
}
