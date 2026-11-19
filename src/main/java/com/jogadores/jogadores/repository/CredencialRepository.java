package com.jogadores.jogadores.repository;

import com.jogadores.jogadores.model.Credencial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredencialRepository extends JpaRepository<Credencial, Integer> {
}
