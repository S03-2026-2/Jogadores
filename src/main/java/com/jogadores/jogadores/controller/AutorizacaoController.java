package com.jogadores.jogadores.controller;

import com.jogadores.jogadores.dto.AutorizacaoResponse;
import com.jogadores.jogadores.dto.VerificarAutorizacaoRequest;
import com.jogadores.jogadores.service.AutorizacaoService;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/autorizacoes")
public class AutorizacaoController {

    private static final String PREFIXO_BEARER = "Bearer ";

    private final AutorizacaoService autorizacaoService;

    public AutorizacaoController(AutorizacaoService autorizacaoService) {
        this.autorizacaoService = autorizacaoService;
    }

    @PostMapping("/verificar")
    public AutorizacaoResponse verificar(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestBody VerificarAutorizacaoRequest request) {

        return autorizacaoService.verificarAutorizacao(
                extrairToken(authorization),
                request.acao(),
                request.jogadorIdAlvo());
    }

    private String extrairToken(String authorization) {
        if (authorization == null || !authorization.startsWith(PREFIXO_BEARER)) {
            return null;
        }
        return authorization.substring(PREFIXO_BEARER.length());
    }
}
