package com.jogadores.jogadores.security;

import com.jogadores.jogadores.exception.AutenticacaoException;
import com.jogadores.jogadores.service.AutenticacaoService;

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

@Component
public class FiltroAutenticacao extends OncePerRequestFilter {

    private static final String PREFIXO_BEARER = "Bearer ";

    private final AutenticacaoService autenticacaoService;

    public FiltroAutenticacao(
            AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        String authorization =
                request.getHeader(HttpHeaders.AUTHORIZATION);

        SecurityContextHolder.clearContext();

        if (authorization != null
                && authorization.startsWith(PREFIXO_BEARER)) {

            String token = authorization.substring(
                    PREFIXO_BEARER.length());

            try {
                JogadorAutenticado jogador =
                        autenticacaoService.autenticarToken(token);

                var autenticacao =
                        new UsernamePasswordAuthenticationToken(
                                jogador.jogadorId(),
                                null,
                                List.of(new SimpleGrantedAuthority(
                                        "ROLE_" + jogador.papel().name()))
                        );

                SecurityContextHolder.getContext()
                        .setAuthentication(autenticacao);

            } catch (AutenticacaoException e) {
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(request, response);
    }
}
