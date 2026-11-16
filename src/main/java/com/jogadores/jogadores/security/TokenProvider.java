package com.jogadores.jogadores.security;

import com.jogadores.jogadores.model.Papel;
import java.time.LocalDateTime;

public interface TokenProvider {

    String gerarToken(int jogadorId, Papel papel);

    boolean validarToken(String token);

    int extrairJogadorId(String token);

    Papel extrairPapel(String token);

    LocalDateTime extrairEmitidoEm(String token);
}
