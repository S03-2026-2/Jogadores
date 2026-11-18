package com.jogadores.jogadores.evento;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class BrokerEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(BrokerEventPublisher.class);

    private final RestClient restClient;
    private final String brokerUrl;

    public BrokerEventPublisher(RestClient.Builder builder, @Value("${broker.url}") String brokerUrl) {
        this.restClient = builder.build();
        this.brokerUrl = brokerUrl;
    }

    @Override
    public void publicar(Evento evento) {
        try {
            restClient.post()
                    .uri(brokerUrl + "/publicar")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("topico", evento.topico(), "dados", evento))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            // O cadastro não depende do Broker
            log.warn("Falha ao publicar evento no tópico {}: {}", evento.topico(), e.getMessage());
        }
    }
}
