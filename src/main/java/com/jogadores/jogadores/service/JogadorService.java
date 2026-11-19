package com.jogadores.jogadores.service;

import com.jogadores.jogadores.dto.AtualizarJogadorRequest;
import com.jogadores.jogadores.dto.CadastroJogadorRequest;
import com.jogadores.jogadores.dto.JogadorResponse;
import com.jogadores.jogadores.evento.EventPublisher;
import com.jogadores.jogadores.evento.JogadorCadastradoEvent;
import com.jogadores.jogadores.model.Acao;
import com.jogadores.jogadores.model.Jogador;
import com.jogadores.jogadores.repository.JogadorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class JogadorService {

    private final JogadorRepository jogadorRepository;
    private final CodificadorSenha codificadorSenha;
    private final EventPublisher eventPublisher;
    private final AutorizacaoService autorizacaoService;

    public JogadorService(
            JogadorRepository jogadorRepository,
            CodificadorSenha codificadorSenha,
            EventPublisher eventPublisher,
            AutorizacaoService autorizacaoService) {
        this.jogadorRepository = jogadorRepository;
        this.codificadorSenha = codificadorSenha;
        this.eventPublisher = eventPublisher;
        this.autorizacaoService = autorizacaoService;
    }

    // Sem @Transactional: o evento só sai depois que o jogador já está gravado
    public JogadorResponse cadastrarJogador(CadastroJogadorRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Corpo da requisição é obrigatório");
        }
        String nome = obrigatorio(req.nome(), "nome").strip();
        String email = obrigatorio(req.email(), "email").strip();
        String senha = obrigatorio(req.senha(), "senha");

        if (jogadorRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }

        // O construtor de Jogador já cria DadosJogador e Credencial (composição 1:1)
        Jogador jogador = jogadorRepository.save(new Jogador(nome, email, senha, codificadorSenha));

        eventPublisher.publicar(new JogadorCadastradoEvent(
                jogador.getId(),
                jogador.getNome(),
                jogador.getEmail(),
                jogador.getDados().getDataCriacao()));

        return JogadorResponse.de(jogador);
    }

    public JogadorResponse consultarDados(String token, int jogadorIdAlvo) {
        verificarAutorizacao(token, Acao.CONSULTAR_DADOS, jogadorIdAlvo);

        return JogadorResponse.de(buscarJogador(jogadorIdAlvo));
    }

    // Listar todos os jogadores é tarefa administrativa
    public List<JogadorResponse> listarJogadores(String token) {
        verificarAutorizacao(token, Acao.ACESSAR_PAINEL_ADMIN, null);

        return jogadorRepository.findAll().stream()
                .map(JogadorResponse::de)
                .toList();
    }

    @Transactional
    public void atualizarDados(String token, int jogadorIdAlvo, AtualizarJogadorRequest req) {
        verificarAutorizacao(token, Acao.ATUALIZAR_DADOS, jogadorIdAlvo);

        if (req == null) {
            throw new IllegalArgumentException("Corpo da requisição é obrigatório");
        }
        // Jogador (Pessoa 1) ainda não expõe um método para trocar o nome
        if (req.nome() != null) {
            throw new IllegalArgumentException("Alteração de nome ainda não disponível");
        }

        Jogador jogador = buscarJogador(jogadorIdAlvo);

        if (req.preferencias() != null) {
            jogador.getDados().atualizarPreferencias(req.preferencias());
        }

        jogadorRepository.save(jogador);
    }

    private void verificarAutorizacao(String token, Acao acao, Integer jogadorIdAlvo) {
        if (!autorizacaoService.verificarAutorizacao(token, acao, jogadorIdAlvo).permitida()) {
            throw new SecurityException("Acesso negado");
        }
    }

    private Jogador buscarJogador(int jogadorId) {
        return jogadorRepository.findById(jogadorId)
                .orElseThrow(() -> new NoSuchElementException("Jogador não encontrado"));
    }

    private String obrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Campo obrigatório: " + campo);
        }
        return valor;
    }
}
