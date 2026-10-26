package com.jogadores.jogadores.security;

import com.jogadores.jogadores.model.Papel;

public interface TokenProvider {

    String gerarToken(int jogadorId, Papel papel);

    boolean validarToken(String token);

    int extrairJogadorId(String token);

    Papel extrairPapel(String token);
}
