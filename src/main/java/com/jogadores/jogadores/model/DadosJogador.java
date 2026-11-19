package com.jogadores.jogadores.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "dados_jogador")
public class DadosJogador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 1000)
    private String preferencias;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    protected DadosJogador() {
        // exigido pelo JPA
    }

    public DadosJogador(String preferencias) {
        this.preferencias = preferencias;
        this.dataCriacao = LocalDateTime.now();
    }

    public void atualizarPreferencias(String novasPreferencias) {
        this.preferencias = novasPreferencias;
    }

    public Integer getId() {
        return id;
    }

    public String getPreferencias() {
        return preferencias;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}
