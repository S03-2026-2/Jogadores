package com.jogadores.jogadores.evento;

import java.time.LocalDateTime;

public record JogadorCadastradoEvent(int jogadorId, String nome, String email, LocalDateTime dataCriacao) {
}
