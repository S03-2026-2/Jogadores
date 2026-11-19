package com.jogadores.jogadores.security;

import com.jogadores.jogadores.service.CodificadorSenha;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptCodificadorSenha implements CodificadorSenha {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String codificar(String senha) {
        return encoder.encode(senha);
    }

    @Override
    public boolean confere(String senha, String senhaHash) {
        return encoder.matches(senha, senhaHash);
    }
}
