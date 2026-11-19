package com.jogadores.jogadores.dto;

import java.time.LocalDateTime;

public record TokenResponse(String token, String tipo, LocalDateTime expiraEm, int jogadorId) {
}
