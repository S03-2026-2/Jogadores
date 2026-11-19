package com.jogadores.jogadores.model;

import com.jogadores.jogadores.service.CodificadorSenha;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "jogadores")
public class Jogador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Papel papel;

    // Composição 1:1: os dados nascem e morrem com o jogador
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "dados_id", unique = true)
    private DadosJogador dados;

    // Composição 1:1: todo jogador tem exatamente uma credencial
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    @JoinColumn(name = "credencial_id", nullable = false, unique = true)
    private Credencial credencial;

    protected Jogador() {
        // exigido pelo JPA
    }

    public Jogador(String nome, String email, String senha, CodificadorSenha codificador) {
        this.nome = nome;
        this.email = email;
        this.papel = Papel.JOGADOR;
        this.credencial = new Credencial(senha, codificador);
        this.dados = new DadosJogador("");
    }

    public boolean senhaConfere(String senha, CodificadorSenha codificador) {
        return credencial.isAtiva() && credencial.confere(senha, codificador);
    }

    public void alterarSenha(String novaSenha, CodificadorSenha codificador) {
        credencial.redefinir(novaSenha, codificador);
    }

    // Tokens emitidos antes da última troca de senha deixam de valer.
    // O JWT guarda a emissão em segundos, então a comparação também é em segundos.
    public boolean aceitaTokenEmitidoEm(LocalDateTime emitidoEm) {
        return credencial.isAtiva()
                && !emitidoEm.isBefore(credencial.getDataAtualizacao().truncatedTo(ChronoUnit.SECONDS));
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public Papel getPapel() {
        return papel;
    }

    public DadosJogador getDados() {
        return dados;
    }
}
