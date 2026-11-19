package com.jogadores.jogadores.service;

import com.jogadores.jogadores.dto.AtualizarJogadorRequest;
import com.jogadores.jogadores.dto.AutorizacaoResponse;
import com.jogadores.jogadores.dto.CadastroJogadorRequest;
import com.jogadores.jogadores.dto.JogadorResponse;
import com.jogadores.jogadores.evento.EventPublisher;
import com.jogadores.jogadores.evento.JogadorCadastradoEvent;
import com.jogadores.jogadores.model.Acao;
import com.jogadores.jogadores.model.DadosJogador;
import com.jogadores.jogadores.model.Jogador;
import com.jogadores.jogadores.model.Papel;
import com.jogadores.jogadores.repository.JogadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JogadorServiceTest {

    private static final String TOKEN = "token";
    private static final LocalDateTime CRIACAO = LocalDateTime.of(2026, 11, 19, 10, 0);

    private JogadorRepository repository;
    private EventPublisher eventPublisher;
    private AutorizacaoService autorizacaoService;
    private Jogador jogador;
    private DadosJogador dados;
    private JogadorService service;

    @BeforeEach
    void setUp() {
        repository = mock(JogadorRepository.class);
        eventPublisher = mock(EventPublisher.class);
        autorizacaoService = mock(AutorizacaoService.class);
        service = new JogadorService(repository, mock(CodificadorSenha.class), eventPublisher, autorizacaoService);

        dados = mock(DadosJogador.class);
        when(dados.getDataCriacao()).thenReturn(CRIACAO);
        jogador = mock(Jogador.class);
        when(jogador.getId()).thenReturn(1);
        when(jogador.getNome()).thenReturn("Ash");
        when(jogador.getEmail()).thenReturn("ash@pallet.com");
        when(jogador.getPapel()).thenReturn(Papel.JOGADOR);
        when(jogador.getDados()).thenReturn(dados);
    }

    private void permitir(Acao acao, Integer jogadorIdAlvo, boolean permitida) {
        when(autorizacaoService.verificarAutorizacao(TOKEN, acao, jogadorIdAlvo))
                .thenReturn(new AutorizacaoResponse(1, acao, permitida));
    }

    @Test
    void cadastroValidoSalvaEPublicaEvento() {
        when(repository.existsByEmail("ash@pallet.com")).thenReturn(false);
        when(repository.save(any(Jogador.class))).thenReturn(jogador);

        JogadorResponse resposta = service.cadastrarJogador(
                new CadastroJogadorRequest(" Ash ", "ash@pallet.com", "pikachu123"));

        assertEquals(new JogadorResponse(1, "Ash", "ash@pallet.com", Papel.JOGADOR, CRIACAO), resposta);
        verify(eventPublisher).publicar(new JogadorCadastradoEvent(1, "Ash", "ash@pallet.com", CRIACAO));
    }

    @Test
    void cadastroComEmailRepetidoGera409() {
        when(repository.existsByEmail("ash@pallet.com")).thenReturn(true);

        var erro = assertThrows(ResponseStatusException.class, () -> service.cadastrarJogador(
                new CadastroJogadorRequest("Ash", "ash@pallet.com", "pikachu123")));

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publicar(any());
    }

    @Test
    void cadastroSemCampoObrigatorioEhRecusado() {
        assertThrows(IllegalArgumentException.class, () -> service.cadastrarJogador(
                new CadastroJogadorRequest("  ", "ash@pallet.com", "pikachu123")));
        assertThrows(IllegalArgumentException.class, () -> service.cadastrarJogador(
                new CadastroJogadorRequest("Ash", null, "pikachu123")));
        assertThrows(IllegalArgumentException.class, () -> service.cadastrarJogador(
                new CadastroJogadorRequest("Ash", "ash@pallet.com", "")));
        assertThrows(IllegalArgumentException.class, () -> service.cadastrarJogador(null));

        verify(repository, never()).save(any());
    }

    @Test
    void consultarDadosDevolveJogador() {
        permitir(Acao.CONSULTAR_DADOS, 1, true);
        when(repository.findById(1)).thenReturn(Optional.of(jogador));

        JogadorResponse resposta = service.consultarDados(TOKEN, 1);

        assertEquals(1, resposta.id());
        assertEquals(CRIACAO, resposta.dataCriacao());
    }

    @Test
    void consultarDadosSemPermissaoEhNegado() {
        permitir(Acao.CONSULTAR_DADOS, 2, false);

        assertThrows(SecurityException.class, () -> service.consultarDados(TOKEN, 2));
        verify(repository, never()).findById(anyInt());
    }

    @Test
    void consultarJogadorInexistenteGera404() {
        permitir(Acao.CONSULTAR_DADOS, 99, true);
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.consultarDados(TOKEN, 99));
    }

    @Test
    void listarJogadoresExigePermissaoDeAdmin() {
        permitir(Acao.ACESSAR_PAINEL_ADMIN, null, true);
        when(repository.findAll()).thenReturn(List.of(jogador));

        assertEquals(1, service.listarJogadores(TOKEN).size());
    }

    @Test
    void jogadorComumNaoListaJogadores() {
        permitir(Acao.ACESSAR_PAINEL_ADMIN, null, false);

        assertThrows(SecurityException.class, () -> service.listarJogadores(TOKEN));
        verify(repository, never()).findAll();
    }

    @Test
    void atualizarDadosTrocaPreferencias() {
        permitir(Acao.ATUALIZAR_DADOS, 1, true);
        when(repository.findById(1)).thenReturn(Optional.of(jogador));

        service.atualizarDados(TOKEN, 1, new AtualizarJogadorRequest(null, "modo noturno"));

        verify(dados).atualizarPreferencias("modo noturno");
        verify(repository).save(jogador);
    }

    @Test
    void atualizarDadosDeOutroJogadorEhNegado() {
        permitir(Acao.ATUALIZAR_DADOS, 2, false);

        assertThrows(SecurityException.class,
                () -> service.atualizarDados(TOKEN, 2, new AtualizarJogadorRequest(null, "x")));
        verify(repository, never()).save(any());
    }
}
