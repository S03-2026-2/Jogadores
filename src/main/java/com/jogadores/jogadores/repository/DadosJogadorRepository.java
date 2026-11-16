package com.jogadores.jogadores.repository;

import com.jogadores.jogadores.model.DadosJogador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DadosJogadorRepository extends JpaRepository<DadosJogador, Integer> {
}
