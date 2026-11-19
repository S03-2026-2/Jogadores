package com.jogadores.jogadores.config;

import com.jogadores.jogadores.model.Acao;
import com.jogadores.jogadores.model.Autorizacao;
import com.jogadores.jogadores.model.Papel;
import com.jogadores.jogadores.repository.AutorizacaoRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Carga inicial da tabela de permissões (Papel × Acao).
 * JOGADOR pode tudo, menos acessar o painel administrativo; ADMIN pode tudo.
 * Se a tabela já tiver dados, nada é feito.
 */
@Component
public class CargaPermissoes implements CommandLineRunner {

    private final AutorizacaoRepository autorizacaoRepository;

    public CargaPermissoes(AutorizacaoRepository autorizacaoRepository) {
        this.autorizacaoRepository = autorizacaoRepository;
    }

    @Override
    public void run(String... args) {
        if (autorizacaoRepository.count() > 0) {
            return;
        }

        List<Autorizacao> permissoes = new ArrayList<>();

        for (Acao acao : Acao.values()) {
            permissoes.add(new Autorizacao(Papel.ADMIN, acao));

            if (acao != Acao.ACESSAR_PAINEL_ADMIN) {
                permissoes.add(new Autorizacao(Papel.JOGADOR, acao));
            }
        }

        autorizacaoRepository.saveAll(permissoes);
    }
}
