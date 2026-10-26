package com.jogadores.jogadores.model;

import com.jogadores.jogadores.service.CodificadorSenha;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "credenciais")
public class Credencial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String senhaHash;

    @Column(nullable = false)
    private boolean ativa;

    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;

    protected Credencial() {
    }

    public Credencial(String senha, CodificadorSenha codificador) {
        this.senhaHash = codificador.codificar(senha);
        this.ativa = true;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public boolean confere(String senha, CodificadorSenha codificador) {
        return codificador.confere(senha, this.senhaHash);
    }

    public void redefinir(String novaSenha, CodificadorSenha codificador) {
        this.senhaHash = codificador.codificar(novaSenha);
        this.dataAtualizacao = LocalDateTime.now();
    }

    public boolean isAtiva() {
        return ativa;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
}
