package com.jogadores.jogadores.evento;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record JogadorCadastradoEvent(int jogadorId, String nome, String email, LocalDateTime dataCriacao)
        implements Evento {

    public static final String TOPICO = "jogador.criado";

    // Fora do JSON: o tópico vai no envelope, não dentro de "dados"
    @Override
    @JsonIgnore
    public String topico() {
        return TOPICO;
    }
}
