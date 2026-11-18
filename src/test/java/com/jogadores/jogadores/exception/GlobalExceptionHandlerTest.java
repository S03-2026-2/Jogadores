package com.jogadores.jogadores.exception;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

class GlobalExceptionHandlerTest {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    static class NaoAutenticado extends RuntimeException {
        NaoAutenticado(String mensagem) {
            super(mensagem);
        }
    }

    @RestController
    static class RotasDeTeste {
        @GetMapping("/anotada")
        void anotada() {
            throw new NaoAutenticado("Token inválido ou expirado");
        }

        @GetMapping("/argumento")
        void argumento() {
            throw new IllegalArgumentException("Dado inválido");
        }

        @GetMapping("/seguranca")
        void seguranca() {
            throw new SecurityException("Acesso negado");
        }

        @GetMapping("/ausente")
        void ausente() {
            throw new NoSuchElementException("Jogador não encontrado");
        }

        @GetMapping("/integridade")
        void integridade() {
            throw new DataIntegrityViolationException("could not execute statement [insert into jogadores ...]");
        }

        @GetMapping("/status")
        void status() {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }

        @GetMapping("/inesperado")
        void inesperado() {
            throw new RuntimeException("senha do banco: 123");
        }

        @PostMapping("/corpo")
        void corpo(@RequestBody Map<String, Object> corpo) {
        }
    }

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new RotasDeTeste())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void statusAnotadoViraErroResponse() throws Exception {
        mvc.perform(get("/anotada"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.erro").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.mensagem").value("Token inválido ou expirado"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void illegalArgumentVira400() throws Exception {
        mvc.perform(get("/argumento"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("BAD_REQUEST"));
    }

    @Test
    void securityExceptionVira403() throws Exception {
        mvc.perform(get("/seguranca"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.erro").value("FORBIDDEN"));
    }

    @Test
    void noSuchElementVira404() throws Exception {
        mvc.perform(get("/ausente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("NOT_FOUND"));
    }

    @Test
    void violacaoDeIntegridadeVira409SemSql() throws Exception {
        mvc.perform(get("/integridade"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem", not(containsString("insert"))));
    }

    @Test
    void responseStatusExceptionUsaOProprioStatus() throws Exception {
        mvc.perform(get("/status"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("E-mail já cadastrado"));
    }

    @Test
    void corpoMalformadoVira400() throws Exception {
        mvc.perform(post("/corpo").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Corpo da requisição inválido"));
    }

    @Test
    void erroInesperadoVira500SemDetalhes() throws Exception {
        mvc.perform(get("/inesperado"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensagem").value("Erro interno no servidor"));
    }
}
