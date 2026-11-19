package com.jogadores.jogadores.service;

import com.jogadores.jogadores.dto.AutorizacaoResponse;
import com.jogadores.jogadores.exception.AutenticacaoException;
import com.jogadores.jogadores.model.Acao;
import com.jogadores.jogadores.repository.AutorizacaoRepository;
import com.jogadores.jogadores.security.JogadorAutenticado;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AutorizacaoService {

    private final AutenticacaoService autenticacaoService;
    private final AutorizacaoRepository autorizacaoRepository;

    public AutorizacaoService(
            AutenticacaoService autenticacaoService,
            AutorizacaoRepository autorizacaoRepository) {

        this.autenticacaoService = autenticacaoService;
        this.autorizacaoRepository = autorizacaoRepository;
    }

    public AutorizacaoResponse verificarAutorizacao(
            String token,
            Acao acao,
            Integer jogadorIdAlvo) {

        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Token ausente");
        }

        if (acao == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"A ação é obrigatória");
        }

        boolean exigeAlvo = acao == Acao.CONSULTAR_DADOS || acao == Acao.ATUALIZAR_DADOS || acao == Acao.REDEFINIR_SENHA;

        if (exigeAlvo && (jogadorIdAlvo == null || jogadorIdAlvo <= 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"O ID do jogador-alvo é obrigatório");
        }

        JogadorAutenticado solicitante;

        try {
            solicitante = autenticacaoService.autenticarToken(token);
        } catch (AutenticacaoException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Token inválido ou expirado", e);
        }

        boolean papelPermite =
                autorizacaoRepository.existsByPapelAndAcao(
                        solicitante.papel(), acao);

        boolean podeAgirSobreAlvo =
                jogadorIdAlvo == null
                        || solicitante.ehDono(jogadorIdAlvo)
                        || solicitante.ehAdmin();

        return new AutorizacaoResponse(
                solicitante.jogadorId(),
                acao,
                papelPermite && podeAgirSobreAlvo
        );
    }
}
