package com.jogadores.jogadores.security;

import com.jogadores.jogadores.model.Papel;

public record JogadorAutenticado(int jogadorId, Papel papel) {

    public boolean ehDono(int jogadorIdAlvo) {
        return jogadorId == jogadorIdAlvo;
    }

    public boolean ehAdmin() {
        return papel == Papel.ADMIN;
    }
}
