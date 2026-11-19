package com.jogadores.jogadores.dto;

import com.jogadores.jogadores.model.Jogador;
import com.jogadores.jogadores.model.Papel;

import java.time.LocalDateTime;

public record JogadorResponse(int id, String nome, String email, Papel papel, LocalDateTime dataCriacao) {

    public static JogadorResponse de(Jogador jogador) {
        return new JogadorResponse(
                jogador.getId(),
                jogador.getNome(),
                jogador.getEmail(),
                jogador.getPapel(),
                jogador.getDados().getDataCriacao());
    }
}
