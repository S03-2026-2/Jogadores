package com.jogadores.jogadores.service;

import com.jogadores.jogadores.dto.LoginRequest;
import com.jogadores.jogadores.dto.RedefinirSenhaRequest;
import com.jogadores.jogadores.dto.TokenResponse;
import com.jogadores.jogadores.dto.ValidacaoTokenResponse;
import com.jogadores.jogadores.exception.AutenticacaoException;
import com.jogadores.jogadores.model.Jogador;
import com.jogadores.jogadores.model.Papel;
import com.jogadores.jogadores.repository.JogadorRepository;
import com.jogadores.jogadores.security.JogadorAutenticado;
import com.jogadores.jogadores.security.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AutenticacaoServiceTest {

    private JogadorRepository repository;
    private CodificadorSenha codificador;
    private TokenProvider tokenProvider;
    private Jogador jogador;
    private AutenticacaoService service;

    @BeforeEach
    void setUp() {
        repository = mock(JogadorRepository.class);
        codificador = mock(CodificadorSenha.class);
        tokenProvider = mock(TokenProvider.class);
        jogador = mock(Jogador.class);
        when(jogador.getId()).thenReturn(1);
        when(jogador.getPapel()).thenReturn(Papel.JOGADOR);
        service = new AutenticacaoService(repository, codificador, tokenProvider, 60);
    }

    @Test
    void loginCorretoDevolveToken() {
        when(repository.findByEmail("ash@pallet.com")).thenReturn(Optional.of(jogador));
        when(jogador.senhaConfere("pikachu123", codificador)).thenReturn(true);
        when(tokenProvider.gerarToken(1, Papel.JOGADOR)).thenReturn("abc");

        TokenResponse resposta = service.autenticar(new LoginRequest("ash@pallet.com", "pikachu123"));

        assertEquals("abc", resposta.token());
        assertEquals("Bearer", resposta.tipo());
        assertEquals(1, resposta.jogadorId());
    }

    @Test
    void loginFalhaComAMesmaMensagemParaEmailOuSenhaIncorretos() {
        when(repository.findByEmail("ash@pallet.com")).thenReturn(Optional.of(jogador));
        when(repository.findByEmail("nao@existe.com")).thenReturn(Optional.empty());
        when(jogador.senhaConfere("errada", codificador)).thenReturn(false);

        String senhaErrada = assertThrows(AutenticacaoException.class,
                () -> service.autenticar(new LoginRequest("ash@pallet.com", "errada"))).getMessage();
        String emailErrado = assertThrows(AutenticacaoException.class,
                () -> service.autenticar(new LoginRequest("nao@existe.com", "errada"))).getMessage();

        assertEquals(senhaErrada, emailErrado);
    }

    @Test
    void tokenValidoDevolveIdEPapel() {
        tokenAceito(true);

        ValidacaoTokenResponse resposta = service.validarToken("abc");

        assertTrue(resposta.valido());
        assertEquals(1, resposta.jogadorId());
        assertEquals(Papel.JOGADOR, resposta.papel());
    }

    @Test
    void tokenAusenteOuAssinaturaInvalidaNaoEValido() {
        when(tokenProvider.validarToken("ruim")).thenReturn(false);

        assertFalse(service.validarToken(null).valido());
        assertFalse(service.validarToken("ruim").valido());
    }

    @Test
    void tokenAnteriorATrocaDeSenhaERejeitado() {
        tokenAceito(false);

        assertFalse(service.validarToken("abc").valido());
        assertThrows(AutenticacaoException.class, () -> service.autenticarToken("abc"));
    }

    @Test
    void redefinirSenhaComSenhaAtualCorretaSalva() {
        when(repository.findById(1)).thenReturn(Optional.of(jogador));
        when(jogador.senhaConfere("atual", codificador)).thenReturn(true);

        service.redefinirSenha(new JogadorAutenticado(1, Papel.JOGADOR),
                new RedefinirSenhaRequest("atual", "nova"));

        verify(jogador).alterarSenha("atual", "nova", codificador);
        verify(repository).save(jogador);
    }

    @Test
    void redefinirSenhaComSenhaAtualIncorretaNaoAltera() {
        when(repository.findById(1)).thenReturn(Optional.of(jogador));
        when(jogador.senhaConfere("errada", codificador)).thenReturn(false);

        assertThrows(AutenticacaoException.class, () -> service.redefinirSenha(
                new JogadorAutenticado(1, Papel.JOGADOR), new RedefinirSenhaRequest("errada", "nova")));

        verify(jogador, never()).alterarSenha(any(), any(), any());
        verify(repository, never()).save(any());
    }

    private void tokenAceito(boolean aceito) {
        LocalDateTime emitidoEm = LocalDateTime.of(2026, 11, 5, 10, 0);
        when(tokenProvider.validarToken("abc")).thenReturn(true);
        when(tokenProvider.extrairJogadorId("abc")).thenReturn(1);
        when(tokenProvider.extrairPapel("abc")).thenReturn(Papel.JOGADOR);
        when(tokenProvider.extrairEmitidoEm("abc")).thenReturn(emitidoEm);
        when(repository.findById(1)).thenReturn(Optional.of(jogador));
        when(jogador.aceitaTokenEmitidoEm(emitidoEm)).thenReturn(aceito);
    }
}
