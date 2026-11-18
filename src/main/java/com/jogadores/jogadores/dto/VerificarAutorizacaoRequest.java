package com.jogadores.jogadores.dto;

import com.jogadores.jogadores.model.Acao;

/**
 * Corpo de POST /autorizacoes/verificar.
 * jogadorIdAlvo é opcional: só vem quando a ação é sobre um jogador específico.
 */
public record VerificarAutorizacaoRequest(
        Acao acao,
        Integer jogadorIdAlvo
) {}
