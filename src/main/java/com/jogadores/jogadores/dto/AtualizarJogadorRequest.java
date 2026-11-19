package com.jogadores.jogadores.dto;

/**
 * Corpo de PUT /jogadores/{id}.
 * Campos nulos são mantidos como estão.
 */
public record AtualizarJogadorRequest(String nome, String preferencias) {
}
