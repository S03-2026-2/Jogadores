package com.jogadores.jogadores.repository;

import com.jogadores.jogadores.model.Jogador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JogadorRepository extends JpaRepository<Jogador, Integer> {

    Optional<Jogador> findByEmail(String email);

    boolean existsByEmail(String email);
}
