package com.jogadores.jogadores.controller;

import com.jogadores.jogadores.dto.AtualizarJogadorRequest;
import com.jogadores.jogadores.dto.CadastroJogadorRequest;
import com.jogadores.jogadores.dto.JogadorResponse;
import com.jogadores.jogadores.service.JogadorService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/jogadores")
public class JogadorController {

    private static final String PREFIXO_BEARER = "Bearer ";

    private final JogadorService jogadorService;

    public JogadorController(JogadorService jogadorService) {
        this.jogadorService = jogadorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JogadorResponse cadastrar(@RequestBody CadastroJogadorRequest req) {
        return jogadorService.cadastrarJogador(req);
    }

    @GetMapping
    public List<JogadorResponse> listar(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String header) {
        return jogadorService.listarJogadores(extrairToken(header));
    }

    @GetMapping("/{id}")
    public JogadorResponse consultar(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String header,
            @PathVariable int id) {
        return jogadorService.consultarDados(extrairToken(header), id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizar(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String header,
            @PathVariable int id,
            @RequestBody AtualizarJogadorRequest req) {
        jogadorService.atualizarDados(extrairToken(header), id, req);
    }

    private String extrairToken(String header) {
        if (header == null || !header.startsWith(PREFIXO_BEARER)) {
            return null;
        }
        return header.substring(PREFIXO_BEARER.length());
    }
}
