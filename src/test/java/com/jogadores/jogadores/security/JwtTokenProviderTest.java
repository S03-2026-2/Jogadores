package com.jogadores.jogadores.security;

import com.jogadores.jogadores.model.Papel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void configurar() {
        String chave = Base64.getEncoder().encodeToString(
                "0123456789abcdef0123456789abcdef"
                        .getBytes(StandardCharsets.UTF_8)
        );

        tokenProvider = new JwtTokenProvider(chave, 60);
    }

    @Test
    void deveGerarTokenValido() {
        String token = tokenProvider.gerarToken(1, Papel.JOGADOR);

        assertNotNull(token);
        assertTrue(tokenProvider.validarToken(token));
        assertEquals(1, tokenProvider.extrairJogadorId(token));
        assertEquals(Papel.JOGADOR, tokenProvider.extrairPapel(token));
    }

    @Test
    void deveRejeitarTokenExpirado() {
        String chave = Base64.getEncoder().encodeToString(
                "0123456789abcdef0123456789abcdef"
                        .getBytes(StandardCharsets.UTF_8)
        );

        JwtTokenProvider providerExpirado =
                new JwtTokenProvider(chave, -1);

        String token = providerExpirado.gerarToken(1, Papel.JOGADOR);

        assertFalse(tokenProvider.validarToken(token));
    }

    @Test
    void deveRejeitarTokenComAssinaturaInvalida() {
        String outraChave = Base64.getEncoder().encodeToString(
                "abcdef0123456789abcdef0123456789"
                        .getBytes(StandardCharsets.UTF_8)
        );

        JwtTokenProvider outroProvider =
                new JwtTokenProvider(outraChave, 60);

        String token = outroProvider.gerarToken(1, Papel.JOGADOR);

        assertFalse(tokenProvider.validarToken(token));
    }

    @Test
    void deveRejeitarTokenMalformado() {
        assertFalse(tokenProvider.validarToken("token.invalido"));
    }

    @Test
    void deveRejeitarTokenNulo() {
        assertFalse(tokenProvider.validarToken(null));
    }

    @Test
    void deveExtrairDataDeEmissao() {
        LocalDateTime antes = LocalDateTime.now();

        String token = tokenProvider.gerarToken(1, Papel.JOGADOR);

        LocalDateTime emitidoEm =
                tokenProvider.extrairEmitidoEm(token);

        LocalDateTime depois = LocalDateTime.now();

        assertNotNull(emitidoEm);

        assertTrue(
                Duration.between(emitidoEm, antes).abs().getSeconds() <= 2
        );

        assertFalse(emitidoEm.isAfter(depois));
    }
}

