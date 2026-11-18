package com.jogadores.jogadores.model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "autorizacoes",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"papel", "acao"}
    )
)
public class Autorizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Papel papel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Acao acao;

    // construtores
    protected Autorizacao() {
    }

    public Autorizacao(Papel papel, Acao acao) {
        this.papel = papel;
        this.acao = acao;
    }

    // getters
    public Long getId() {
        return id;
    }

    public Papel getPapel() {
        return papel;
    }

    public Acao getAcao() {
        return acao;
    }

}
