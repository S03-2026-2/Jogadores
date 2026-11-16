package com.jogadores.jogadores.repository;

import com.jogadores.jogadores.model.Acao;
import com.jogadores.jogadores.model.Autorizacao;
import com.jogadores.jogadores.model.Papel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorizacaoRepository
        extends JpaRepository<Autorizacao, Long> {

    boolean existsByPapelAndAcao(Papel papel, Acao acao);
}
