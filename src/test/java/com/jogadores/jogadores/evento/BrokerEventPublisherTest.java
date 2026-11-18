package com.jogadores.jogadores.evento;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withAccepted;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;

import java.net.ConnectException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class BrokerEventPublisherTest {

    private final JogadorCadastradoEvent evento = new JogadorCadastradoEvent(
            1, "Ash Ketchum", "ash@pallet.com", LocalDateTime.of(2026, 10, 18, 14, 30));

    private MockRestServiceServer server;
    private BrokerEventPublisher publisher;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        publisher = new BrokerEventPublisher(builder, "http://broker");
    }

    @Test
    void publicaNoTopicoDoEvento() {
        server.expect(requestTo("http://broker/publicar"))
                .andExpect(method(POST))
                .andExpect(jsonPath("$.topico").value("jogador.criado"))
                .andExpect(jsonPath("$.dados.jogadorId").value(1))
                .andExpect(jsonPath("$.dados.email").value("ash@pallet.com"))
                .andExpect(jsonPath("$.dados.topico").doesNotExist())
                .andRespond(withAccepted());

        publisher.publicar(evento);

        server.verify();
    }

    @Test
    void falhaDoBrokerNaoInterrompeOCadastro() {
        server.expect(requestTo("http://broker/publicar")).andRespond(withServerError());

        assertDoesNotThrow(() -> publisher.publicar(evento));
    }

    @Test
    void brokerForaDoArNaoInterrompeOCadastro() {
        server.expect(requestTo("http://broker/publicar"))
                .andRespond(withException(new ConnectException("recusado")));

        assertDoesNotThrow(() -> publisher.publicar(evento));
    }

    @Test
    void barraFinalNaUrlNaoDuplicaNoCaminho() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        publisher = new BrokerEventPublisher(builder, "http://broker/");
        server.expect(requestTo("http://broker/publicar")).andRespond(withAccepted());

        publisher.publicar(evento);

        server.verify();
    }

    @Test
    void urlInvalidaDoBrokerNaoInterrompeOCadastro() {
        BrokerEventPublisher semEsquema = new BrokerEventPublisher(RestClient.builder(), "localhost:8000");

        assertDoesNotThrow(() -> semEsquema.publicar(evento));
    }
}
