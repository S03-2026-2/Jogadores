package com.jogadores.jogadores.model;

import com.jogadores.jogadores.service.CodificadorSenha;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa a comparação real de datas em aceitaTokenEmitidoEm, sem mocks.
 */
class JogadorTest {

    private final CodificadorSenha codificador = new CodificadorSenha() {
        @Override
        public String codificar(String senha) {
            return "hash:" + senha;
        }

        @Override
        public boolean confere(String senha, String senhaHash) {
            return senhaHash.equals("hash:" + senha);
        }
    };

    private Jogador jogador;

    @BeforeEach
    void configurar() {
        jogador = new Jogador("Ash", "ash@pokemon.com", "senha123", codificador);
    }

    @Test
    void tokenEmitidoDepoisDaUltimaTrocaEhAceito() throws InterruptedException {
        Thread.sleep(5);

        assertTrue(jogador.aceitaTokenEmitidoEm(LocalDateTime.now()));
    }

    @Test
    void tokenEmitidoAntesDaTrocaDeSenhaEhRecusado() throws InterruptedException {
        LocalDateTime emitidoEm = LocalDateTime.now();
        Thread.sleep(5);

        jogador.alterarSenha("novaSenha", codificador);

        assertFalse(jogador.aceitaTokenEmitidoEm(emitidoEm));
    }

    @Test
    void tokenEmitidoAntesDaTrocaNoMesmoSegundoEhRecusado() throws InterruptedException {
        // Garante que emissão e troca caiam no mesmo segundo
        while (LocalDateTime.now().getNano() > 900_000_000) {
            Thread.sleep(10);
        }

        LocalDateTime emitidoEm = LocalDateTime.now();
        Thread.sleep(5);
        jogador.alterarSenha("novaSenha", codificador);

        assertEquals(emitidoEm.withNano(0), LocalDateTime.now().withNano(0));
        assertFalse(jogador.aceitaTokenEmitidoEm(emitidoEm));
    }

    @Test
    void tokenEmitidoDepoisDaTrocaDeSenhaEhAceito() throws InterruptedException {
        jogador.alterarSenha("novaSenha", codificador);
        Thread.sleep(5);

        assertTrue(jogador.aceitaTokenEmitidoEm(LocalDateTime.now()));
    }
}
