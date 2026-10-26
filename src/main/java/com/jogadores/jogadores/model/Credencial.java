package com.seuprojeto.jogadores.model;

import java.time.LocalDateTime;

public class Credencial{
  private int id;
  private String senhaHash;
  private boolean ativa;
  private LocalDateTime dataAtualizacao;

  public Credencial(){
  }

  public Credencial(int id, String senhaHash, boolean ativa, LocalDateTime dataAtualizacao){
    this.id = id;
    this.senhaHash = senhaHash;
    this.ativa = ativa;
    this.dataAtualizacao = dataAtualizacao;
  }

  
}
