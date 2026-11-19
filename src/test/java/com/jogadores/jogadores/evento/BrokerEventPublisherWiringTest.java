package com.jogadores.jogadores.evento;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.restclient.autoconfigure.RestClientAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

// Trava a dependência do RestClient.Builder: sem o starter a aplicação não sobe
class BrokerEventPublisherWiringTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RestClientAutoConfiguration.class))
            .withUserConfiguration(BrokerEventPublisher.class)
            .withPropertyValues("broker.url=http://broker");

    @Test
    void publisherSobeComRestClientAutoConfigurado() {
        runner.run(context -> assertThat(context).hasSingleBean(BrokerEventPublisher.class));
    }
}
