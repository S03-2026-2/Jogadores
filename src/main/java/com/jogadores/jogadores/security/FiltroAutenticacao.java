package com.jogadores.jogadores.security;

import com.jogadores.jogadores.model.Papel;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Elo da cadeia de filtros do Spring Security (Chain of Responsibility).
 * Se a requisição traz um JWT válido, registra o jogador como autenticado
 * e passa adiante. Se não traz, apenas passa adiante: quem decide se a rota
 * exige autenticação é o SecurityConfig, que responde 401 nesse caso.
 */
@Component
public class FiltroAutenticacao extends OncePerRequestFilter {

    private static final String PREFIXO_BEARER = "Bearer ";

    private final TokenProvider tokenProvider;

    public FiltroAutenticacao(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization != null && authorization.startsWith(PREFIXO_BEARER)) {
            String token = authorization.substring(PREFIXO_BEARER.length());

            if (tokenProvider.validarToken(token)) {
                int jogadorId = tokenProvider.extrairJogadorId(token);
                Papel papel = tokenProvider.extrairPapel(token);

                var autenticacao = new UsernamePasswordAuthenticationToken(
                        jogadorId,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + papel.name())));

                SecurityContextHolder.getContext().setAuthentication(autenticacao);
            }
        }

        chain.doFilter(request, response);
    }
}
