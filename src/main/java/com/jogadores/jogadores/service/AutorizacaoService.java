package com.jogadores.jogadores.service;

import com.jogadores.jogadores.dto.AutorizacaoResponse;
import com.jogadores.jogadores.model.Acao;
import com.jogadores.jogadores.model.Papel;
import com.jogadores.jogadores.repository.AutorizacaoRepository;
import com.jogadores.jogadores.security.TokenProvider;

import org.springframework.stereotype.Service;

@Service
public class AutorizacaoService {

    private final TokenProvider tokenProvider;
    private final AutorizacaoRepository autorizacaoRepository;

    public AutorizacaoService(
            TokenProvider tokenProvider,
            AutorizacaoRepository autorizacaoRepository) {

        this.tokenProvider = tokenProvider;
        this.autorizacaoRepository = autorizacaoRepository;
    }

    public AutorizacaoResponse verificarAutorizacao(
            String token,
            Acao acao) {

        if (!tokenProvider.validarToken(token)) {
            return new AutorizacaoResponse(
                    0,
                    acao,
                    false
            );
        }

        int jogadorId =
                tokenProvider.extrairJogadorId(token);

        Papel papel =
                tokenProvider.extrairPapel(token);

        boolean permitida =
                autorizacaoRepository
                        .existsByPapelAndAcao(papel, acao);

        return new AutorizacaoResponse(
                jogadorId,
                acao,
                permitida
        );
    }
}
