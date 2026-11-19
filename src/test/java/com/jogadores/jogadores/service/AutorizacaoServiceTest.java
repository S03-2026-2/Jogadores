package com.jogadores.jogadores.service;

import com.jogadores.jogadores.dto.AutorizacaoResponse;
import com.jogadores.jogadores.model.Acao;
import com.jogadores.jogadores.model.Papel;
import com.jogadores.jogadores.repository.AutorizacaoRepository;
import com.jogadores.jogadores.security.JogadorAutenticado;
import com.jogadores.jogadores.exception.AutenticacaoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AutorizacaoServiceTest {

    private static final String TOKEN = "token";

    private AutenticacaoService autenticacaoService;
    private AutorizacaoRepository repository;
    private AutorizacaoService service;

    @BeforeEach
    void configurar() {
        autenticacaoService = mock(AutenticacaoService.class);
        repository = mock(AutorizacaoRepository.class);
        service = new AutorizacaoService(autenticacaoService, repository);
    }

    private void tokenDe(int jogadorId, Papel papel) {
        when(autenticacaoService.autenticarToken(TOKEN)).thenReturn(new JogadorAutenticado(jogadorId, papel));
    }

    @Test
    void tokenInvalidoGera401() {
        when(autenticacaoService.autenticarToken(TOKEN)).thenThrow(new AutenticacaoException("Token inválido ou expirado"));
        var erro = assertThrows(ResponseStatusException.class,() -> service.verificarAutorizacao(TOKEN, Acao.PROPOR_TROCA, null));
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

    @Test
    void acoesIndividuaisExigemJogadorAlvo() {
        tokenDe(9, Papel.ADMIN);

        Acao[] acoes = {
            Acao.CONSULTAR_DADOS,
            Acao.ATUALIZAR_DADOS,
            Acao.REDEFINIR_SENHA
        };

        for (Acao acao : acoes) {
            var erro = assertThrows(ResponseStatusException.class, () -> service.verificarAutorizacao(TOKEN, acao, null));
            assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());
        }
    }

    @Test
    void acaoAusenteGera400() {
        tokenDe(1, Papel.JOGADOR);

        var erro = assertThrows(ResponseStatusException.class,() -> service.verificarAutorizacao(TOKEN, null, null));

        assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());
    }
}
