package com.jogadores.jogadores.dto;

import com.jogadores.jogadores.model.Papel;

public record ValidacaoTokenResponse(boolean valido, int jogadorId, Papel papel) {
}
