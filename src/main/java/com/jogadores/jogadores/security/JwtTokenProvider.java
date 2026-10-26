package com.jogadores.jogadores.security;

import com.jogadores.jogadores.model.Papel;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtTokenProvider implements TokenProvider {

    private final SecretKey chaveSecreta;
    private final long expiracaoMinutos;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String chaveSecreta,
            @Value("${jwt.expiracao-minutos}") long expiracaoMinutos) {

        this.chaveSecreta = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(chaveSecreta)
        );

        this.expiracaoMinutos = expiracaoMinutos;
    }

    @Override
    public String gerarToken(int jogadorId, Papel papel) {

        Instant agora = Instant.now();

        Instant expiracao =
                agora.plus(expiracaoMinutos, ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(String.valueOf(jogadorId))
                .claim("papel", papel.name())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(expiracao))
                .signWith(chaveSecreta)
                .compact();
    }

    @Override
    public boolean validarToken(String token) {

        try {

            extrairClaims(token);

            return true;

        } catch (JwtException | IllegalArgumentException e) {

            return false;
        }
    }

    @Override
    public int extrairJogadorId(String token) {

        Claims claims = extrairClaims(token);

        return Integer.parseInt(
                claims.getSubject()
        );
    }

    @Override
    public Papel extrairPapel(String token) {

        Claims claims = extrairClaims(token);

        String papel = claims.get(
                "papel",
                String.class
        );

        return Papel.valueOf(papel);
    }

    private Claims extrairClaims(String token) {

        return Jwts.parser()
                .verifyWith(chaveSecreta)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
