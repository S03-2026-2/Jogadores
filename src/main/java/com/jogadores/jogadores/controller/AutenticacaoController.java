package com.jogadores.jogadores.controller;

import com.jogadores.jogadores.dto.LoginRequest;
import com.jogadores.jogadores.dto.RedefinirSenhaRequest;
import com.jogadores.jogadores.dto.TokenResponse;
import com.jogadores.jogadores.dto.ValidacaoTokenResponse;
import com.jogadores.jogadores.security.JogadorAutenticado;
import com.jogadores.jogadores.service.AutenticacaoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

    private static final String PREFIXO_BEARER = "Bearer ";

    private final AutenticacaoService autenticacaoService;

    public AutenticacaoController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest req) {
        return autenticacaoService.autenticar(req);
    }

    @GetMapping("/validar")
    public ValidacaoTokenResponse validar(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String header) {
        return autenticacaoService.validarToken(extrairToken(header));
    }

    @PutMapping("/senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void redefinirSenha(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String header,
            @RequestBody RedefinirSenhaRequest req) {
        JogadorAutenticado solicitante = autenticacaoService.autenticarToken(extrairToken(header));
        autenticacaoService.redefinirSenha(solicitante, req);
    }

    private String extrairToken(String header) {
        if (header == null || !header.startsWith(PREFIXO_BEARER)) {
            return null;
        }
        return header.substring(PREFIXO_BEARER.length());
    }
}
