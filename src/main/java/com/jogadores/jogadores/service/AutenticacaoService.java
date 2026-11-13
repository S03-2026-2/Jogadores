package com.jogadores.jogadores.service;

import com.jogadores.jogadores.dto.LoginRequest;
import com.jogadores.jogadores.dto.RedefinirSenhaRequest;
import com.jogadores.jogadores.dto.TokenResponse;
import com.jogadores.jogadores.dto.ValidacaoTokenResponse;
import com.jogadores.jogadores.exception.AutenticacaoException;
import com.jogadores.jogadores.model.Jogador;
import com.jogadores.jogadores.repository.JogadorRepository;
import com.jogadores.jogadores.security.JogadorAutenticado;
import com.jogadores.jogadores.security.TokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AutenticacaoService {

    private final JogadorRepository jogadorRepository;
    private final CodificadorSenha codificadorSenha;
    private final TokenProvider tokenProvider;
    private final long expiracaoMinutos;

    public AutenticacaoService(
            JogadorRepository jogadorRepository,
            CodificadorSenha codificadorSenha,
            TokenProvider tokenProvider,
            @Value("${jwt.expiracao-minutos}") long expiracaoMinutos) {
        this.jogadorRepository = jogadorRepository;
        this.codificadorSenha = codificadorSenha;
        this.tokenProvider = tokenProvider;
        this.expiracaoMinutos = expiracaoMinutos;
    }

    public TokenResponse autenticar(LoginRequest req) {
        // A mesma mensagem para e-mail e senha não revela quais e-mails estão cadastrados
        Jogador jogador = jogadorRepository.findByEmail(req.email())
                .filter(j -> j.senhaConfere(req.senha(), codificadorSenha))
                .orElseThrow(() -> new AutenticacaoException("E-mail ou senha incorretos"));

        String token = tokenProvider.gerarToken(jogador.getId(), jogador.getPapel());
        LocalDateTime expiraEm = LocalDateTime.now().plusMinutes(expiracaoMinutos);

        return new TokenResponse(token, "Bearer", expiraEm, jogador.getId());
    }

    public ValidacaoTokenResponse validarToken(String token) {
        return lerToken(token)
                .map(j -> new ValidacaoTokenResponse(true, j.jogadorId(), j.papel()))
                .orElseGet(() -> new ValidacaoTokenResponse(false, 0, null));
    }

    public JogadorAutenticado autenticarToken(String token) {
        return lerToken(token)
                .orElseThrow(() -> new AutenticacaoException("Token inválido ou expirado"));
    }

    @Transactional
    public void redefinirSenha(JogadorAutenticado solicitante, RedefinirSenhaRequest req) {
        Jogador jogador = jogadorRepository.findById(solicitante.jogadorId())
                .filter(j -> j.senhaConfere(req.senhaAtual(), codificadorSenha))
                .orElseThrow(() -> new AutenticacaoException("Senha atual incorreta"));

        jogador.alterarSenha(req.senhaAtual(), req.novaSenha(), codificadorSenha);
        jogadorRepository.save(jogador);
    }

    private Optional<JogadorAutenticado> lerToken(String token) {
        if (token == null || !tokenProvider.validarToken(token)) {
            return Optional.empty();
        }

        int jogadorId = tokenProvider.extrairJogadorId(token);
        LocalDateTime emitidoEm = tokenProvider.extrairEmitidoEm(token);

        // Tokens emitidos antes da última troca de senha deixam de valer
        return jogadorRepository.findById(jogadorId)
                .filter(j -> j.aceitaTokenEmitidoEm(emitidoEm))
                .map(j -> new JogadorAutenticado(jogadorId, tokenProvider.extrairPapel(token)));
    }
}
