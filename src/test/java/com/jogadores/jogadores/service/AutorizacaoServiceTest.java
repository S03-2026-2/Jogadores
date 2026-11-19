package com.jogadores.jogadores.service;

import com.jogadores.jogadores.dto.AutorizacaoResponse;
import com.jogadores.jogadores.model.Acao;
import com.jogadores.jogadores.model.Papel;
import com.jogadores.jogadores.repository.AutorizacaoRepository;
import com.jogadores.jogadores.security.TokenProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AutorizacaoServiceTest {

    private static final String TOKEN = "token";

    private TokenProvider tokenProvider;
    private AutorizacaoRepository repository;
    private AutorizacaoService service;

    @BeforeEach
    void configurar() {
        tokenProvider = mock(TokenProvider.class);
        repository = mock(AutorizacaoRepository.class);
        service = new AutorizacaoService(tokenProvider, repository);
    }

    private void tokenDe(int jogadorId, Papel papel) {
        when(tokenProvider.validarToken(TOKEN)).thenReturn(true);
        when(tokenProvider.extrairJogadorId(TOKEN)).thenReturn(jogadorId);
        when(tokenProvider.extrairPapel(TOKEN)).thenReturn(papel);
    }

    @Test
    void tokenInvalidoGera401() {
        when(tokenProvider.validarToken(TOKEN)).thenReturn(false);

        var erro = assertThrows(ResponseStatusException.class,
                () -> service.verificarAutorizacao(TOKEN, Acao.PROPOR_TROCA, null));

        assertEquals(HttpStatus.UNAUTHORIZED, erro.getStatusCode());
    }

    @Test
    void tokenAusenteGera401() {
        assertThrows(ResponseStatusException.class,
                () -> service.verificarAutorizacao(null, Acao.PROPOR_TROCA, null));
    }

    @Test
    void papelComPermissaoESemAlvoEhPermitido() {
        tokenDe(1, Papel.JOGADOR);
        when(repository.existsByPapelAndAcao(Papel.JOGADOR, Acao.PROPOR_TROCA)).thenReturn(true);

        AutorizacaoResponse r = service.verificarAutorizacao(TOKEN, Acao.PROPOR_TROCA, null);

        assertTrue(r.permitida());
        assertEquals(1, r.jogadorId());
    }

    @Test
    void papelSemPermissaoEhNegado() {
        tokenDe(1, Papel.JOGADOR);
        when(repository.existsByPapelAndAcao(Papel.JOGADOR, Acao.ACESSAR_PAINEL_ADMIN)).thenReturn(false);

        assertFalse(service.verificarAutorizacao(TOKEN, Acao.ACESSAR_PAINEL_ADMIN, null).permitida());
    }

    @Test
    void jogadorPodeAgirSobreSiMesmo() {
        tokenDe(1, Papel.JOGADOR);
        when(repository.existsByPapelAndAcao(Papel.JOGADOR, Acao.ATUALIZAR_DADOS)).thenReturn(true);

        assertTrue(service.verificarAutorizacao(TOKEN, Acao.ATUALIZAR_DADOS, 1).permitida());
    }

    @Test
    void jogadorNaoPodeAgirSobreOutro() {
        tokenDe(1, Papel.JOGADOR);
        when(repository.existsByPapelAndAcao(Papel.JOGADOR, Acao.ATUALIZAR_DADOS)).thenReturn(true);

        assertFalse(service.verificarAutorizacao(TOKEN, Acao.ATUALIZAR_DADOS, 2).permitida());
    }

    @Test
    void adminPodeAgirSobreOutro() {
        tokenDe(9, Papel.ADMIN);
        when(repository.existsByPapelAndAcao(Papel.ADMIN, Acao.ATUALIZAR_DADOS)).thenReturn(true);

        assertTrue(service.verificarAutorizacao(TOKEN, Acao.ATUALIZAR_DADOS, 2).permitida());
    }
}
