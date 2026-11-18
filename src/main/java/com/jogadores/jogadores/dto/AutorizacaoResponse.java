package com.jogadores.jogadores.dto;

import com.jogadores.jogadores.model.Acao;

public record AutorizacaoResponse(
        int jogadorId,
        Acao acao,
        boolean permitida
) {}
