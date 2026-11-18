package com.jogadores.jogadores.service;

import com.jogadores.jogadores.dto.AutorizacaoResponse;
import com.jogadores.jogadores.model.Acao;
import com.jogadores.jogadores.model.Papel;
import com.jogadores.jogadores.repository.AutorizacaoRepository;
import com.jogadores.jogadores.security.TokenProvider;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    /**
     * Verifica se o dono do token pode executar a ação.
     *
     * @param jogadorIdAlvo jogador sobre o qual a ação é feita; null quando a
     *                      ação não envolve outro jogador (ex.: PROPOR_TROCA).
     * @throws ResponseStatusException 401 se o token for ausente ou inválido.
     */
    public AutorizacaoResponse verificarAutorizacao(
            String token,
            Acao acao,
            Integer jogadorIdAlvo) {

        if (token == null || !tokenProvider.validarToken(token)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Token inválido ou expirado");
        }

        int solicitanteId = tokenProvider.extrairJogadorId(token);
        Papel papel = tokenProvider.extrairPapel(token);

        boolean papelPermite =
                autorizacaoRepository.existsByPapelAndAcao(papel, acao);

        boolean podeAgirSobreAlvo =
                jogadorIdAlvo == null
                        || jogadorIdAlvo == solicitanteId
                        || papel == Papel.ADMIN;

        return new AutorizacaoResponse(
                solicitanteId,
                acao,
                papelPermite && podeAgirSobreAlvo
        );
    }
}
