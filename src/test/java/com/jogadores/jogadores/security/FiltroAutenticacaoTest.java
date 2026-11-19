package com.jogadores.jogadores.security;

import com.jogadores.jogadores.exception.AutenticacaoException;
import com.jogadores.jogadores.model.Papel;
import com.jogadores.jogadores.service.AutenticacaoService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FiltroAutenticacaoTest {

    private final AutenticacaoService service =
            mock(AutenticacaoService.class);

    private final FiltroAutenticacao filtro =
            new FiltroAutenticacao(service);

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void tokenValidoAutenticaJogador() throws Exception {
        when(service.autenticarToken("valido"))
                .thenReturn(new JogadorAutenticado(1, Papel.JOGADOR));

        executarFiltro("Bearer valido");

        var autenticacao =
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(autenticacao);
        assertEquals(1, autenticacao.getPrincipal());
        assertTrue(autenticacao.isAuthenticated());
    }

    @Test
    void tokenRevogadoNaoAutentica() throws Exception {
        when(service.autenticarToken("revogado"))
                .thenThrow(new AutenticacaoException("Token inválido"));

        executarFiltro("Bearer revogado");

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );
    }

    @Test
    void semTokenNaoAutentica() throws Exception {
        executarFiltro(null);

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        verifyNoInteractions(service);
    }

    private void executarFiltro(String authorization) throws Exception {
        var request = new MockHttpServletRequest("GET", "/protegida");
        var response = new MockHttpServletResponse();

        if (authorization != null) {
            request.addHeader("Authorization", authorization);
        }

        filtro.doFilter(request, response, new MockFilterChain());
    }
}
